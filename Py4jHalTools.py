import numpy as np
import pandas as pd

"""
to get the openCommand: 
in java program: 
import py4j.GatewayServer

new GatewayServer(JavaObject).start();
System.out.println("gateway started");

the print statement will tell python that the object is prepared and ready to use

start the program in intellij, copy the compilation/run string, and use that as the openCommand

if process can't start because socket is in use:
default socket is 25333
call: "lsof -i:25333" from terminal
then: "kill pid" where pid is the process that the lsof call names
"""



from py4j.java_gateway import JavaGateway
class Py4jObject(JavaGateway):
  def __init__(self, startCommandStr=None, port=25333):
    self.proc=None
    from py4j.java_gateway import GatewayParameters
    from py4j.java_gateway import java_import
    if startCommandStr is None or self.is_port_in_use(port): super().__init__(gateway_parameters=GatewayParameters(auto_field=True, auto_close=True))
    else:
      import subprocess
      self.proc= subprocess.Popen(startCommandStr, shell=True, stdout=subprocess.PIPE)
      while True:
        if self.proc.stdout.readline()is not None:
          super().__init__(gateway_parameters=GatewayParameters(auto_field=True,auto_close=True))
          break
    java_import(self.jvm,'Framework.Util')
    java_import(self.jvm,'Framework.Tools.PY4J.DoublesDataFrame')
  def is_port_in_use(self,port):
    import socket
    with socket.socket(socket.AF_INET, socket.SOCK_STREAM) as s:
      return s.connect_ex(('localhost', port)) == 0


  def __enter__(self):
    return self

  def __exit__(self,execType,execValue,tb):
    if self.proc is not None:
      self.shutdown()
      self.proc.kill()

  def GetNP(self, javaArray):
    data=np.frombuffer(self.jvm.Framework.Util.Py4jDoublesOut(javaArray), dtype=np.float64)
    data.setflags(write=1)
    if javaArray.getClass().getSimpleName()=="double[][]":
      length=len(javaArray)
      return np.reshape(data,(length,-1))

  def PutNP(self, data):
    npArray=np.asarray(data,np.float64)
    if len(npArray.shape)==1:
      return self.jvm.Framework.Util.Py4jDoublesIn(npArray.tobytes(order='C'))
    elif len(npArray.shape)==2:
      return self.jvm.Framework.Util.Py4jDoublesIn(npArray.tobytes(order='C'), npArray.shape[0])
    else:raise Exception("numpy array must be of dimension 2 or lower")

  def PutDF(self,doublesDataFrame):
    doubles=self.PutNP(doublesDataFrame.values)
    cols=doublesDataFrame.columns
    colsArr=self.new_array(self.jvm.String,len(cols))
    for i in range(len(cols)):
      colsArr[i]=str(cols[i])
    return self.jvm.Framework.Tools.PY4J.DoublesDataFrame(colsArr,doubles)

  def GetDF(self,doublesDataFrame,headers=None):
    if headers is None:
      doubles=self.GetNP(doublesDataFrame.GetData)
      cols=doublesDataFrame.GetHeaders
    else:
      doubles=self.GetNP(doublesDataFrame)
      cols=headers
    return pd.DataFrame(data=doubles,columns=cols)

  def GetDF(self,javaArray,headers):
    doubles=self.GetNP(javaArray)
    return pd.DataFrame(data=doubles,columns=headers)

  def GetStrings(self,javaArray):
    return list(javaArray)

  def PutStrings(self,strings):
    javaArr=self.new_array(self.jvm.String,len(strings))
    for i,string in enumerate(strings):
      javaArr[i]=string
    return javaArr

class Py4jModelSweeper(Py4jObject):
  #JavaGateway object must implement Py4jRunner to work
  #SetupModel should take as arguments: (MoistLab,Model) -> InitializedModel
  def __init__(self, startCommandStr=None, SetupModelFn=None, port=25333):
    super().__init__(startCommandStr, port)
    self.ResetModelPool()
    if SetupModelFn is not None:
      self.SetupModel=SetupModelFn.__get__(self, Py4jModelSweeper)
      self.SetupModel(self.AddPoolModel())

  def AddSetup(self, NewSetupModelFn):
    self.SetupModel=NewSetupModelFn.__get__(self, Py4jModelSweeper)
    self.ResetModelPool()
    self.SetupModel(self.AddPoolModel())

  def Sweep(self,params,nThreads=1,resultsDF=None):
    for i in range(self.NumPoolModels(),nThreads):
      newModel=self.AddPoolModel()
      self.SetupModel(newModel)
    out=self.GetNP(self.EvalGen(self.PutNP(params),nThreads))
    refModel=self.GetModelFromPool()
    res=pd.DataFrame(data=np.hstack((params,out)),columns=list(refModel.GetParamHeaders())+list(refModel.GetResultHeaders()))
    if resultsDF is None:
      return res
    else:
      return resultsDF.append(res,ignore_index=True)

  def GenTestModel(self,params):
    model=self.SetupModel(self.GenModel())
    model.Reset(self.PutNP(params))
    return model

  def RescaleParams(self,params):
    refModel=self.GetModelFromPool()
    return self.GetNP(refModel.RescaleParams(self.PutNP(params)))




