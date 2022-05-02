package data.campaign.rulecmd;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.*;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.campaign.rules.MemoryAPI;
import com.fs.starfarer.api.impl.campaign.intel.deciv.DecivTracker;
import com.fs.starfarer.api.impl.campaign.procgen.themes.BaseThemeGenerator;
import com.fs.starfarer.api.impl.campaign.procgen.themes.MiscellaneousThemeGenerator;
import com.fs.starfarer.api.impl.campaign.rulecmd.BaseCommandPlugin;
import com.fs.starfarer.api.util.Misc;
import org.lazywizard.lazylib.campaign.CampaignUtils;
import org.apache.log4j.*;

import java.util.List;
import java.util.Map;

public class nukePlanet_RULECMD extends BaseCommandPlugin {

    private static org.apache.log4j.Logger log = Global.getLogger(nukePlanet_RULECMD.class);

    //note: this constant is an arbitrary number, in the future maybe have it dynamic depending on the planet

    int PLANET_DEBRIS_RADIUS=400;

    @Override
    public boolean execute(String ruleId,
                           InteractionDialogAPI dialog,
                           List<Misc.Token> params,
                           Map<String, MemoryAPI> memoryMap) {
        if (dialog == null) return false;

        log.info("so this nukeplanet script is running, right.");

        //note: first we get the planet to nuke
        SectorEntityToken planet  = (SectorEntityToken) dialog.getInteractionTarget();
        //note: we need to know the system to call removeEntity, so let's get that from planet
        StarSystemAPI system=planet.getStarSystem();
        log.info("logging planet location.");
        //note: print int for good luck
        log.info(42);
        //note: hurray, I can print Vector2 directly
        LocationAPI planetLoc=planet.getContainingLocation();
        log.info(planetLoc);

        //note: then we get the market
        MarketAPI market = planet.getMarket();

        //note: If there is no market, we'll ignore that and proceed straight to nuking.
        //if (market!=null) //note: actually that doesn't work, market is never null, even empty planets have markets.

        //note: todo: remove rings and coronas from planet

        FactionAPI neutral = Global.getSector().getFaction("neutral");
        final float searchRange =5500f;//arbitrary number, in the future have this bigger but have a check to avoid removing the terrain from unrelated planets
        List<SectorEntityToken> terrainList=CampaignUtils.getNearbyEntitiesFromFaction(planet, searchRange, "terrain", neutral);

            //note: god i fucking hope terrain is neutral, and that people don't fuck with terrain faction identity
            //note: holy shit it works
            //note: wait fuck, it wipes magfields but not rings that are in search range
        for (SectorEntityToken terrain:terrainList)
        {
            if(terrain.hasTag("planet")||terrain.hasTag("star")||terrain.hasTag("moon"))
            {
                log.info("found a planet, star, or moon that shouldn't be deleted here");
            }
            else{
                log.info("deleting some rings or whatever, hopefully");
                system.removeEntity(terrain);
            }
        }

        //note: todo remove nascent gravity well

       // List<NascentGravityWellAPI>gravwells=system.getGravityWells();
        //note: okay, now to find the one that matches the planet
        //note: java 7 is too obsolete to use allMatch, so foreach it is
//        for (NascentGravityWellAPI well:gravwells)
//        {
//            //note: isInCurrentLocation doesn't work because the well is in hyper, not realspace
//            if(well.getLocationInHyperspace() == planet.getLocationInHyperspace());
//            {
//                system.removeEntity(well);
//                break;
//            }
//        }

        //note: let's just test removing all wells to see what happens

//        log.info("Logging gravwell locations.");
//        for (NascentGravityWellAPI well:gravwells)
//        {
//            //note: this isn't printing anything, suggesting that it isn't finding the gravwells or iterating through them
//            log.info("The location of this well is:");
//            log.info(well.getLocation());
//            system.removeEntity(well);
//        }

       //note: nerp I have to iterate through every fuckin' entity, doesn't seem to be a more performant way

        List<SectorEntityToken>allEntities=Global.getSector().getHyperspace().getAllEntities();
        for (SectorEntityToken entity:allEntities)
        {
            if (entity instanceof NascentGravityWellAPI&&entity!=null){
                log.info("Looking for nascent gravity wells to purge.");
                //note: isInCurrentLocation doesn't work because the well is in hyper, not realspace
                NascentGravityWellAPI nascwell= (NascentGravityWellAPI) entity; //note: the cast works, it just protests in game if you forget to do the instanceOf check like an idiot
                if (nascwell.getTarget() == planet){
                    //note: it's nothing in this code block that's causing the cast problem
                    log.info("Purging nascent gravity well.");
                    LocationAPI location = nascwell.getContainingLocation();

                    log.info("nascent grav well's location is" + location.getLocation());
                    location.removeEntity(nascwell);

//                    else{
//                    //note: fuck it let's check again.
                      //note: okay, so nascwell is indeed in nascwell's location
//                    List<NascentGravityWellAPI> welllist=location.getGravityWells();
//                    if(nascwell==welllist.get(0)) {
//                        log.info("nascent grav well's location is" + location.getLocation());
//                        location.removeEntity(nascwell);
//                    }
//                    else{
//                        log.info("something is severely fucked up if the location from nascwell isn't nascwell's location");
//                    }
                    break;
                }
            }
        }

        //note: hartley suggested that doing the same loop but through terrain might help
//
//        List <CampaignTerrainAPI> listOfNearbyTerrain=system.getTerrainCopy();
//        for (CampaignTerrainAPI terrain:listOfNearbyTerrain)
//        {
//            log.info("Looking for nascent gravity wells to purge.");
//            //note: isInCurrentLocation doesn't work because the well is in hyper, not realspace
//            if(terrain.getLocationInHyperspace() == planet.getLocationInHyperspace()&&terrain instanceof NascentGravityWellAPI);
//            {
//                log.info("Purging nascent gravity well.");
//
//                //note: what if we just teleport this somewhere else and hope it don't matter
//
//                terrain.setLocation(-10000,-10000);
//
//                //note: shrinking things small doesn't seem to work either
//                final float tinyRadius =0.1f;
//                terrain.setRadius(tinyRadius);
//
//                LocationAPI location = terrain.getContainingLocation();
//                log.info("nascent grav well's location is" + location.getLocation());
//                //note: this doesn't work for whatever reason
//                location.removeEntity(terrain);
//                break;
//            }
//        }


        //note: I'm hoping that this takes care of all connected stations.
        //note: It does!
        for (SectorEntityToken entity : market.getConnectedEntities()) {
            system.removeEntity(entity);
            //note: force deciv, should remove market for real
            //note: full destroy, full destroy withIntel
            //note: naw, that's unnecessary, force deciv is enough
        }

        //note: okay, what if I try removing all nascent gravity wells and then regenerating all of them except for the one I need to destroy

        //note: I guess this is the same as removing connected entities, probably it is the same in the underlying sense.
        //note: nope, it doesn't remove stations.
//        planet.getContainingLocation().removeEntity(planet);


        DecivTracker decivtracker=DecivTracker.getInstance(); //note: wait, this is a static object I think? I shouldn't need to do this???
        decivtracker.decivilize(market, true, true);


        //note: spawn debris around planet
                //note: In retrospect I could have also used the magicCampaign function for exactly this.

        MiscellaneousThemeGenerator MiscGen = new MiscellaneousThemeGenerator(); //note: The generator doesn't *seem* to be static, though the methods are?
        BaseThemeGenerator.StarSystemData sysData = MiscGen.computeSystemData(system);
            //note: todo could make debris radius dependent on the kind of target, currently it's a constant.
            //note: todo make debris field rewards dependent on planet. eg. add some volatiles for a gas/cryo planet
        MiscGen.addDebrisField(sysData, planet, PLANET_DEBRIS_RADIUS);

        //note: todo spawn asteroid field around planet

        //note: destroy planet

        system.removeEntity(planet);

        //note: todo make the explosion effect that happens when a battle is won. I have no idea where to do this.


        //note: sanity check that planet is gone

        if (planet==null)
        {
            return true;
        }
        else{
            log.error("Attempted but failed to delete a planet for some reason!");
            return false; //note: I should make returning false actually trigger an exception check
        }


    }
}


