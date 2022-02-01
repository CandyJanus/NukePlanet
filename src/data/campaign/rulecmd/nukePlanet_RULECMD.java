package data.campaign.rulecmd;

import com.fs.starfarer.api.campaign.InteractionDialogAPI;
import com.fs.starfarer.api.campaign.SectorEntityToken;
import com.fs.starfarer.api.campaign.StarSystemAPI;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.campaign.rules.MemoryAPI;
import com.fs.starfarer.api.impl.campaign.intel.deciv.DecivTracker;
import com.fs.starfarer.api.impl.campaign.procgen.themes.BaseThemeGenerator;
import com.fs.starfarer.api.impl.campaign.procgen.themes.MiscellaneousThemeGenerator;
import com.fs.starfarer.api.impl.campaign.rulecmd.BaseCommandPlugin;
import com.fs.starfarer.api.util.Misc;

import java.util.List;
import java.util.Map;

public class nukePlanet_RULECMD extends BaseCommandPlugin {

    //note: this constant is an arbitrary number

    int PLANET_DEBRIS_RADIUS=400;

    @Override
    public boolean execute(String ruleId,
                           InteractionDialogAPI dialog,
                           List<Misc.Token> params,
                           Map<String, MemoryAPI> memoryMap) {
        if (dialog == null) return false;

        //note: first we get the planet to nuke
        SectorEntityToken planet  = (SectorEntityToken) dialog.getInteractionTarget();
        //note: we need to know the system to call removeEntity, so let's get that from planet
        StarSystemAPI system=planet.getStarSystem();

        //note: then we get the market
        MarketAPI market = planet.getMarket();

        //note: If there is no market, we'll ignore that and proceed straight to nuking.
        //if (market!=null) //note: actually that doesn't work, market is never null, even empty planets have markets.

            //note: I'm hoping that this takes care of all connected stations.
            //note: It does!
        for (SectorEntityToken entity : market.getConnectedEntities()) {
            system.removeEntity(entity);
            //note: force deciv, should remove market for real
            //note: full destroy, full destroy withIntel
        }
        DecivTracker decivtracker=DecivTracker.getInstance(); //note: wait, this is a static object I think? I shouldn't need to do this???
        decivtracker.decivilize(market, true, true);


        //note: todo: remove rings and coronas from planet

        //note: todo remove nascent gravity well

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


