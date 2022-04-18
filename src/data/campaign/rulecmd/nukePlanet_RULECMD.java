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
        log.info(planet.getLocation());

        //note: then we get the market
        MarketAPI market = planet.getMarket();

        //note: If there is no market, we'll ignore that and proceed straight to nuking.
        //if (market!=null) //note: actually that doesn't work, market is never null, even empty planets have markets.

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

//        List<SectorEntityToken>allEntities=Global.getSector().getHyperspace().getAllEntities();
//        for (SectorEntityToken entity:allEntities)
//        {
//            log.info("Looking for nascent gravity wells to purge.");
//            //note: isInCurrentLocation doesn't work because the well is in hyper, not realspace
//            if(entity.getLocationInHyperspace() == planet.getLocationInHyperspace()&&entity instanceof NascentGravityWellAPI);
//            {
//                log.info("Purging nascent gravity well.");
//                LocationAPI location = entity.getContainingLocation();
//                log.info("nascent grav well's location is" + location.getLocation());
//                location.removeEntity(entity);
//                break;
//            }
//        }

        //note: hartley suggested that doing the same loop but through terrain might help

        List <CampaignTerrainAPI> terrainList=system.getTerrainCopy();
        for (CampaignTerrainAPI terrain:terrainList)
        {
            log.info("Looking for nascent gravity wells to purge.");
            //note: isInCurrentLocation doesn't work because the well is in hyper, not realspace
            if(terrain.getLocationInHyperspace() == planet.getLocationInHyperspace()&&terrain instanceof NascentGravityWellAPI);
            {
                log.info("Purging nascent gravity well.");

                //note: what if we just teleport this somewhere else and hope it don't matter

                terrain.setLocation(-10000,-10000);
                terrain.updateSpec();

                //note: shrinking things small doesn't seem to work either
//                final float tinyRadius =0.1f;
//                terrain.setRadius(tinyRadius);

//                LocationAPI location = terrain.getContainingLocation();
//                log.info("nascent grav well's location is" + location.getLocation());
                //note: this doesn't work for whatever reason
                //location.removeEntity(terrain);
                break;
            }
        }


        //note: I'm hoping that this takes care of all connected stations.
        //note: It does!
        for (SectorEntityToken entity : market.getConnectedEntities()) {
            system.removeEntity(entity);
            //note: force deciv, should remove market for real
            //note: full destroy, full destroy withIntel
            //note: naw, that's unnecessary, force deciv is enough
        }

        //note: I guess this is the same as removing connected entities, probably it is the same in the underlying sense.
        //note: nope, it doesn't remove stations.
//        planet.getContainingLocation().removeEntity(planet);


        DecivTracker decivtracker=DecivTracker.getInstance(); //note: wait, this is a static object I think? I shouldn't need to do this???
        decivtracker.decivilize(market, true, true);


        //note: todo: remove rings and coronas from planet


        //note: spawn debris around planet
                //note: In retrospect I could have also used the magicCampaign function for exactly this.

        MiscellaneousThemeGenerator MiscGen = new MiscellaneousThemeGenerator(); //note: The generator doesn't *seem* to be static, though the methods are?
        BaseThemeGenerator.StarSystemData sysData = MiscGen.computeSystemData(system);
            //note: todo could make debris radius dependent on the kind of target, currently it's a constant.
            //note: todo make debris field rewards dependent on planet. eg. add some volatiles for a gas/cryo planet
        //MiscGen.addDebrisField(sysData, planet, PLANET_DEBRIS_RADIUS);

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

            return false; //note: wait, does returning false throw an exception anywhere, or
        }


    }
}


