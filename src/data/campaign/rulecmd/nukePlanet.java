package data.campaign.rulecmd;

import com.fs.starfarer.api.campaign.InteractionDialogAPI;
import com.fs.starfarer.api.campaign.SectorEntityToken;
import com.fs.starfarer.api.campaign.StarSystemAPI;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.campaign.rules.MemoryAPI;
import com.fs.starfarer.api.impl.campaign.intel.deciv.DecivTracker;
import com.fs.starfarer.api.impl.campaign.rulecmd.BaseCommandPlugin;
import com.fs.starfarer.api.util.Misc;

import java.util.List;
import java.util.Map;

public class nukePlanet extends BaseCommandPlugin {

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
        if (market!=null) {
            //note: I'm hoping that this takes care of all connected stations.
            //note: It does!
            for (SectorEntityToken entity : market.getConnectedEntities()) {
                system.removeEntity(entity);
            }
        }

        //note: force deciv, should remove market for real
            //note: full destroy, full destroy withIntel

        DecivTracker decivtracker=DecivTracker.getInstance(); //note: wait, this is a static object I think? I shouldn't need to do this???
        decivtracker.decivilize(market, true, true);

        //note: spawn debris around planet

        //todo

        //note: destroy planet

        system.removeEntity(planet);

        //note: remove nascent gravity well

        //todo

        //note: sanity check that planet is gone

        //todo

        return true;
    }
}


