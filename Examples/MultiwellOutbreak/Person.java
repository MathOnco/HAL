package Examples.MultiwellOutbreak;

import HAL.GridsAndAgents.AgentSQ2Dunstackable;

//cells grow and mutate
class Person extends AgentSQ2Dunstackable<OutbreakWorld>{

    public final static int SUSCEPTIBLE = 0;
    public final static int DEAD = 1;
    public final static int RECOVERED = 2;
    public final static int ASYMPTOMATIC_INFECTED = 3;
    public final static int SYMPTOMATIC_INFECTED = 4;

    // cell attributes: type; future_type; fitness
    int type;
    int future_type;
    boolean quarantined;

    // count-down timers until next state:
    int day_counter;


    public void Init(int type) {
        this.type = type;
        this.future_type = type;
        this.quarantined = false;
        this.day_counter = 0;
        G.counts[type]++;
    }

    void IncrementCounters() {
        day_counter++;

        if ((this.type == ASYMPTOMATIC_INFECTED) && (day_counter >= G.TIME_UNTIL_SYMPTOMATIC)) {
            this.future_type = SYMPTOMATIC_INFECTED;
            day_counter = 0;
        }
        if ((this.type == SYMPTOMATIC_INFECTED) && (day_counter >= G.TIME_UNTIL_RECOVERED)) {
            this.future_type = RECOVERED;
            day_counter = 0;
        }
    }

    // assumes you are infected, checks neighborhood to see if you infect someone else
    void InfectOthers() {
        double infection_rate = G.TRANSMISSION;
        int nNeighbors = this.MapOccupiedHood(G.hood);

        for (int i = 0; i < G.ENCOUNTERS_PER_DAY; i++) {
            Person thisNeighbor = G.GetAgent(G.hood[G.rn.Int(nNeighbors)]); // random neighbor in hood
            if ((thisNeighbor.type == SUSCEPTIBLE) && (!thisNeighbor.quarantined)) {
                if ((G.rn.Double() < infection_rate)) {
                    thisNeighbor.future_type = Person.ASYMPTOMATIC_INFECTED;
                }
            }
        }
    }
}