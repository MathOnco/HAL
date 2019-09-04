package HAL.Gui;

//
//  GifMaker.java
//
//  Created by Elliot Kroo on 2009-04-25.
//
// This work is licensed under the Creative Commons Attribution 3.0 Unported
// License. To view a copy of this license, visit
// http://creativecommons.org/licenses/by/3.0/ or send a letter to Creative
// Commons, 171 Second Street, Suite 300, San Francisco, California, 94105, USA.


import javax.imageio.*;
import javax.imageio.metadata.*;
import javax.imageio.stream.*;
import java.awt.*;
import java.awt.image.*;
import java.io.*;
import java.util.Iterator;

import static java.awt.image.BufferedImage.TYPE_INT_RGB;

/**
 * the GifMaker is used to create gif files by compiling UIGrid states
 */
public class GifMaker {
    protected ImageWriter gifWriter;
    protected ImageWriteParam imageWriteParam;
    protected IIOMetadata imageMetaData;
    protected File file;
    protected BufferedImage scaledBuff;
    protected Graphics2D g;

    /**
     * Creates a new GifMaker
     *
     * @param outputPath        the gif file to be written to
     * @param timeBetweenFramesMS the time between frames in miliseconds
     * @param loopContinuously    wether the gif should loop repeatedly
     * @throws IIOException if no gif ImageWriters are found
     * @author Elliot Kroo (elliot[at]kroo[dot]net)
     */
    public GifMaker(
            String outputPath,
            int timeBetweenFramesMS,
            boolean loopContinuously) {
        // my method to create a writer
        try {
            gifWriter = getWriter();
        }
        catch (Exception e){
            System.err.println("unable to create a new gifWriter");
        }
        imageWriteParam = gifWriter.getDefaultWriteParam();
        ImageTypeSpecifier imageTypeSpecifier =
                ImageTypeSpecifier.createFromBufferedImageType(TYPE_INT_RGB);

        imageMetaData =
                gifWriter.getDefaultImageMetadata(imageTypeSpecifier,
                        imageWriteParam);

        String metaFormatName = imageMetaData.getNativeMetadataFormatName();

        IIOMetadataNode root = (IIOMetadataNode)
                imageMetaData.getAsTree(metaFormatName);

        IIOMetadataNode graphicsControlExtensionNode = getNode(
                root,
                "GraphicControlExtension");

        graphicsControlExtensionNode.setAttribute("disposalMethod", "none");
        graphicsControlExtensionNode.setAttribute("userInputFlag", "FALSE");
        graphicsControlExtensionNode.setAttribute(
                "transparentColorFlag",
                "FALSE");
        graphicsControlExtensionNode.setAttribute(
                "delayTime",
                Integer.toString(timeBetweenFramesMS / 10));
        graphicsControlExtensionNode.setAttribute(
                "transparentColorIndex",
                "0");

        IIOMetadataNode commentsNode = getNode(root, "CommentExtensions");
        commentsNode.setAttribute("CommentExtension", "Created by MAH");

        IIOMetadataNode appEntensionsNode = getNode(
                root,
                "ApplicationExtensions");

        IIOMetadataNode child = new IIOMetadataNode("ApplicationExtension");

        child.setAttribute("applicationID", "NETSCAPE");
        child.setAttribute("authenticationCode", "2.0");

        int loop = loopContinuously ? 0 : 1;

        child.setUserObject(new byte[]{0x1, (byte) (loop & 0xFF), (byte)
                ((loop >> 8) & 0xFF)});
        appEntensionsNode.appendChild(child);

        try {
            imageMetaData.setFromTree(metaFormatName, root);
        }
        catch (Exception e){
            System.err.println("unable to set image metadata");
        }
        file=new File(outputPath);

        try {
            gifWriter.setOutput(new FileImageOutputStream(new File(outputPath)));
        }
        catch (Exception e){
            System.err.println("unable to write gif output");
        }

        try {
            gifWriter.prepareWriteSequence(null);
        }catch (Exception e){
            System.err.println("unable to prepare write sequence");
        }
    }

    /**
     * adds the current UIGrid state to the GIF as a single frame
     */
    public void AddFrame(UIGrid vis){
        if(scaledBuff==null||scaledBuff.getHeight()!=vis.panel.scaleX *vis.yDim||scaledBuff.getWidth()!=vis.panel.scaleX *vis.xDim) {
            scaledBuff = new BufferedImage(vis.panel.scaleX * vis.xDim, vis.panel.scaleX * vis.yDim, BufferedImage.TYPE_INT_RGB);
            g=scaledBuff.createGraphics();
        }
        g.drawImage((vis.panel.buff.getScaledInstance(vis.panel.scaleX *vis.xDim,-vis.panel.scaleX *vis.yDim,Image.SCALE_FAST)),0,0,null);
        RenderedImage img=scaledBuff;
        //RenderedImage img= (RenderedImage)(vis.panel.buff.getScaledInstance(vis.panel.scaleX*vis.xDim,-vis.panel.scaleX*vis.yDim,Image.SCALE_FAST));
        try {
            gifWriter.writeToSequence(
                    new IIOImage(

                            img
,
                            null,
                            imageMetaData),
                    imageWriteParam);
        }
        catch (Exception e){
            System.err.println("unable to write UIGrid to gif sequence");
        }
    }

//    public void AddFrame(UIWindow vis){
//        if(scaledBuff==null||scaledBuff.getHeight()!=vis.panel.getHeight()||scaledBuff.getWidth()!=vis.panel.getWidth()) {
//            scaledBuff = new BufferedImage(vis.panel.getHeight(), vis.panel.getWidth(), BufferedImage.TYPE_INT_RGB);
//            g=scaledBuff.createGraphics();
//        }
//        vis.panel.paint(g);
//        RenderedImage img=scaledBuff;
//        //RenderedImage img= (RenderedImage)(vis.panel.buff.getScaledInstance(vis.panel.scaleX*vis.xDim,-vis.panel.scaleX*vis.yDim,Image.SCALE_FAST));
//        try {
//            gifWriter.writeToSequence(
//                    new IIOImage(
//
//                            img
//                            ,
//                            null,
//                            imageMetaData),
//                    imageWriteParam);
//        }
//        catch (Exception e){
//            System.err.println("unable to write UIWindow to gif sequence");
//        }
//    }
    /**
     * Close this GifMaker object
     */
    public void Close() {
        try {
            gifWriter.endWriteSequence();
        }
        catch (Exception e){
            System.err.println("unable to end gif sequence");
        }
    }

    /**
     * Returns the first available GIF ImageWriter using
     * ImageIO.getImageWritersBySuffix("gif").
     *
     * @return a GIF ImageWriter object
     * @throws IIOException if no GIF image writers are returned
     */
    private static ImageWriter getWriter() throws IIOException {
        Iterator<ImageWriter> iter = ImageIO.getImageWritersBySuffix("gif");
        if (!iter.hasNext()) {
            throw new IIOException("No GIF Image Writers Exist");
        } else {
            return iter.next();
        }
    }

    /**
     * Returns an existing child node, or creates and returns a new child node (if
     * the requested node does not exist).
     *
     * @param rootNode the <tt>IIOMetadataNode</tt> to search for the child node.
     * @param nodeName the name of the child node.
     * @return the child node, if found or a new node created with the given name.
     */
    private static IIOMetadataNode getNode(
            IIOMetadataNode rootNode,
            String nodeName) {
        int nNodes = rootNode.getLength();
        for (int i = 0; i < nNodes; i++) {
            if (rootNode.item(i).getNodeName().compareToIgnoreCase(nodeName)
                    == 0) {
                return ((IIOMetadataNode) rootNode.item(i));
            }
        }
        IIOMetadataNode node = new IIOMetadataNode(nodeName);
        rootNode.appendChild(node);
        return (node);
    }
}

