package Testing;
import static Testing.GLOBALS.*;

/**
 * Created by rafael on 7/5/17.
 * world layout:
 * AgentSQ2unstackable
 * "contact inhibtion" will over time prevent cells from doing anything, leaving most in occassionally broken stasis
 * agents move "simultaneously", calculating an ideal movement path, then choosing from the remainders if that path is taken
 * agent movement calc is done with an int[5] that computes the directional movement layout
 */

class GLOBALS{
    int stay=0;
    int up=1;
    int down=2;
    int left=3;
    int right=4;
}

public class CancerCraft {

    ////TODO: add multiplayer support
////add A* pathfinding, with corner optimization
////
//
//import Framework.Interfaces.*;
//import Framework.Extensions.CircleForceAgent2;
//import Framework.GridsAndAgents.AgentGrid2D;
//import Framework.Gui.GuiGrid;
//import Framework.Gui.GuiWindow;
//
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.SetPix;
//
//import static Framework.Utils.*;
//
///**
// * Created by rafael on 7/3/17.
// */
//
//
///*cancercraft will work in the following manner
//Neutral Players
//    [-Vessels-] can migrate and branch
//    [-Fibroblasts-] crawl randomly like worms, are indestructible.
//        (fibroblasts won't generally wander far from their local hood)
//
//units are immune system
//tumor has >fibroblasts,>vessels, >converted immune as resources
//player determines >tumor movement, growth happens automatically
//depending on what happens to tumor, 6 different phenotypic evolutionary paths are possible:
//(they exist on a triangle), with 3 of them being combinationns of the other 2
//(cancer is presented as the primary color combo of the phenotypes)
//
//green) proliferation: sit in place, access to resources speeds this up easy to get lots of these
//yellow) acid producing and acid resistant: creates a wave of death
//red) movement: become metastatic, happens when cells move
//magenta) become subversive, and invisible to the enemy:
//blue) recruiting: happens due to combat, they can convert! allows cancer cells to attract fibroblasts and vessels
//cyan) resistant/inhibitory: cells develop the ability to fight in place
//
//immune system:
//    more conventional style) 3 resources: fibroblasts, vessels, antigen
//    units) lymph nodes: offscreen, produce units that teleport in through vessels, vessels can be clicked at any time to produce units
//    tertiary lymphoid structures: exist on the world itself, produce units and collect resources, must be called from vessels, can also warp in through vessels
//    basically offscreen lymph nodes and lymphoid structures are
//    Innate[costs no antigen, responds to antibody
//        [-normal cells-] (will push out cancer cells in small numbers, blocks tumor growth)
//        fever
//        chemical signals
//        inflamation
//
//        AntigenPresentingCell{
//            collect antigen presenting cell like harvester, cancer cells are found when they return
//        }
//
//        phagocytes
//            [-neutrophils-] fast grunts, produce chemokine during battle
//            [-macrophages-] (tanks), exist on a spectrum, produce chemokine during battle
//                start M1, but are converted to M2 by eating dead tissue
//                become cafs if they are converted while in M2
//                in M2 they can help tumor/normal tissue grow back, but can become tumor associated if converted
//            [-natural killer cells-] can detect hidden, won't die after attacking, uses "poision"
//
//        inflamation
//            (happens when normal cells die)
//            causes stuff to come in from vessels, makes immune casting from vessels faster
//    ]
//    Specific[costs antigen, responds to antibody
//        antibodies, slows cancer, and makes them easily targeted by Innate
//        [-BCell-] can detect cancer cells, unless they are highly subversive/resistant
//            consume chemokine to produce antibodies for a period. can also be triggered by T cell produced chemokine from fights are fighting
//        [-TCell-] neotank, won't die after attacking
//    ]
//
//    Diffusibles[
//        [-O2-] resource
//        [-Acid-] biproduct
//        [-Chemokine-] T,neutrophil
//    ]
//
//
//
//game is played with guigridvis, mouse clicks and keyboard
//gui is a guigridvis imbedded in a guiwindow
//    */
}
