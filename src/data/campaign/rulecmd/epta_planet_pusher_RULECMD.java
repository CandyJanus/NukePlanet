//package data.campaign.rulecmd;
//
//import com.fs.starfarer.api.Global;
//import com.fs.starfarer.api.campaign.*;
//import com.fs.starfarer.api.campaign.econ.MarketAPI;
//import com.fs.starfarer.api.campaign.rules.MemoryAPI;
//import com.fs.starfarer.api.impl.campaign.intel.deciv.DecivTracker;
//import com.fs.starfarer.api.impl.campaign.procgen.themes.BaseThemeGenerator;
//import com.fs.starfarer.api.impl.campaign.procgen.themes.MiscellaneousThemeGenerator;
//import com.fs.starfarer.api.impl.campaign.rulecmd.BaseCommandPlugin;
//import com.fs.starfarer.api.util.Misc;
//
//import java.awt.*;
//import java.util.Iterator;
//import java.util.List;
//import java.util.Map;
//
////note: This script is a modified version of the VIC viral bombardment code, with express permission from PureTilt, the coder. Immeasurable thanks to him.
//
//public class epta_planet_pusher_RULECMD extends BaseCommandPlugin {
//
//    public static String
//            ENGAGE = "mktEngage",
//            ShowPlanetPushMenu = "ShowPlanetPushMenu",
//            PlanetPushMenu = "eptaPlanetPushMenu",
//            PlanetPushConfirm = "eptaPlanetPushConfirm",
//            PlanetPushResults = "eptaPlanetPushResults",
//            GO_BACK = "mktGoBack",
//    //note: salvage gantry is temporary to test this. When we implement this for real, we'll have our own hull mod.
//            HULLMOD_TO_CHECK = "repair_gantry";
//    protected CampaignFleetAPI playerFleet;
//    protected SectorEntityToken entity;
//    protected FactionAPI playerFaction;
//    protected FactionAPI entityFaction;
//    protected TextPanelAPI text;
//    protected OptionPanelAPI options;
//    protected CargoAPI playerCargo;
//    protected MemoryAPI memory;
//    protected MarketAPI market;
//    protected InteractionDialogAPI dialog;
//    protected Map<String, MemoryAPI> memoryMap;
//    protected FactionAPI faction;
//
//    public epta_planet_pusher_RULECMD(){
//    }
//
//    public epta_planet_pusher_RULECMD(SectorEntityToken entity){
//        init(entity);
//    }
//
//    //note: It is entirely intentional that ground defenses and hazard have no influence on planet-push cost. You are, after all, destroying an occupied and non-resisting planet.
//
//    public static int getBombardmentCost(MarketAPI market, CampaignFleetAPI fleet) {
//        float planetSize = 150f; //note: used if market on station
//        if (market.getPlanetEntity() != null){
//            planetSize = market.getPlanetEntity().getRadius();
//            if (planetSize > 350) planetSize = 350;
//            if (planetSize< 50) planetSize = 50;
//        }
//        float cost=planetSize*10;
//        return Math.round(cost);
//    }
//
//    //note: VIC added tooltip code. I don't actually know why, but I'm not going to bother implementing it until I know why it would be necessary. I'm not implementing any way to make planet-pushing cheaper.
//
////    public static TooltipMakerAPI.StatModValueGetter statPrinter(final boolean withNegative) {
////        return new TooltipMakerAPI.StatModValueGetter() {
////            public String getPercentValue(MutableStat.StatMod mod) {
////                String prefix = mod.getValue() > 0 ? "+" : "";
////                return prefix + (int) (mod.getValue()) + "%";
////            }
////
////            public String getMultValue(MutableStat.StatMod mod) {
////                return Strings.X + "" + Misc.getRoundedValue(mod.getValue());
////            }
////
////            public String getFlatValue(MutableStat.StatMod mod) {
////                String prefix = mod.getValue() > 0 ? "+" : "";
////                return prefix + (int) (mod.getValue()) + "";
////            }
////
////            public Color getModColor(MutableStat.StatMod mod) {
////                if (withNegative && mod.getValue() < 1f) return Misc.getNegativeHighlightColor();
////                return null;
////            }
////        };
////    }
//
//    //note: to be perfectly honest I don't understand what's going on here
//    public static void addBombardVisual(SectorEntityToken target) {
//        if (target != null && target.isInCurrentLocation()) {
//            int num = (int) Math.round((3.15 * Math.pow(target.getRadius(), 2)) / 800);
//            num *= 2;
//            if (num > 200) num = 200;
//            if (num < 10) num = 10;
//            //note: I'll implement the animations later
////            target.addScript(new epta_planet_pusher_RULECMD.InitialPlanetPushAnimation(num, target));
////            target.addScript(new epta_planet_pusher_RULECMD.PlanetBeingPushedAnimation(num, target));
//        }
//    }
//
//    //note: I don't know why VIC has this.
//    protected void clearTemp() {
//        if (temp != null) {
//            temp.raidLoot = null;
//            temp.raidValuables = null;
//            temp.target = null;
//            //temp.willBecomeHostile.clear();
//        }
//    }
//
//    protected void init(SectorEntityToken entity) {
//
//        memory = entity.getMemoryWithoutUpdate();
//        this.entity = entity;
//        playerFleet = Global.getSector().getPlayerFleet();
//        playerCargo = playerFleet.getCargo();
//
//        playerFaction = Global.getSector().getPlayerFaction();
//        entityFaction = entity.getFaction();
//
//        faction = entity.getFaction();
//
//        market = entity.getMarket();
//
//
//        //DebugFlags.MARKET_HOSTILITIES_DEBUG = false;
//        //market.getMemoryWithoutUpdate().set(MemFlags.MEMORY_KEY_PLAYER_HOSTILE_ACTIVITY_NEAR_MARKET, true, 0.1f);
//
//    }
//
//    public boolean execute(String ruleId, InteractionDialogAPI dialog, List<Token> params, Map<String, MemoryAPI> memoryMap) {
//        //super.execute(ruleId, dialog, params, memoryMap);
//
//        this.dialog = dialog;
//        this.memoryMap = memoryMap;
//
//        String command = params.get(0).getString(memoryMap);
//        if (command == null) return false;
//
//        entity = dialog.getInteractionTarget();
//        init(entity);
//
//        memory = getEntityMemory(memoryMap);
//
//        text = dialog.getTextPanel();
//        options = dialog.getOptionPanel();
//
//        switch (command) {
//            case "ShowPlanetPushMenu":
//                clearTemp();
//                ShowPlanetPushMenu();
//                break;
//            case "PlanetPushMenu":
//                PlanetPushMenu();
//                break;
//            case "PlanetPushConfirm":
//                PlanetPushConfirm();
//                break;
//            case "PlanetPushResults":
//                PlanetPushResults();
//                break;
//        }
//
//        return true;
//
//    }
//
//    //note: ah fuck it I'll finish this later
//    protected void ShowPlanetPushMenu() {
//        options.clearOptions();
//        CampaignFleetAPI primary = getInteractionTargetForFIDPI();
//        CampaignFleetAPI station = getStationFleet();
//    }
//
//    protected CampaignFleetAPI getInteractionTargetForFIDPI() {
//        CampaignFleetAPI primary = this.getStationFleet();
//        if (primary == null) {
//            CampaignFleetAPI best = null;
//            float minDist = 3.4028235E38F;
//            Iterator i$ = Misc.getNearbyFleets(this.entity, 2000.0F).iterator();
//
//            while(i$.hasNext()) {
//                CampaignFleetAPI fleet = (CampaignFleetAPI)i$.next();
//                if (fleet.getBattle() == null && fleet.getFaction() == this.market.getFaction() && fleet.getFleetData().getNumMembers() > 0) {
//                    float dist = Misc.getDistance(this.entity.getLocation(), fleet.getLocation());
//                    dist -= this.entity.getRadius();
//                    dist -= fleet.getRadius();
//                    if (dist < Misc.getBattleJoinRange() && dist < minDist) {
//                        best = fleet;
//                        minDist = dist;
//                    }
//                }
//            }
//            primary = best;
//        }
//        return primary;
//    }
//
//    protected void PlanetPushMenu() {
//        float width = 350;
//        float opad = 10f;
//        float small = 5f;
//
//        Color h = Misc.getHighlightColor();
//        Color b = Misc.getNegativeHighlightColor();
//
//        dialog.getVisualPanel().showImagePortion("illustrations", "bombard_prepare", 640, 400, 0, 0, 480, 300);
//    }
//
//    protected void PlanetPushConfirm() {
//    }
//
//    protected void PlanetPushResults() {
//    }
//
//    //note: We're going to have to rewrite this into a piece of code that triggers when the radius of the orbit equals the radius of the sun. Probably we won't have it in execute itself.
//    @Override
//    public boolean execute(String ruleId,
//                           InteractionDialogAPI dialog,
//                           List<Misc.Token> params,
//                           Map<String, MemoryAPI> memoryMap) {
//        if (dialog == null) return false;
//
//        //note: first we get the planet to nuke
//        SectorEntityToken planet  = (SectorEntityToken) dialog.getInteractionTarget();
//        //note: we need to know the system to call removeEntity, so let's get that from planet
//        StarSystemAPI system=planet.getStarSystem();
//
//        //note: then we get the market
//        MarketAPI market = planet.getMarket();
//
//        //note: If there is no market, we'll ignore that and proceed straight to nuking.
//        //if (market!=null) //note: actually that doesn't work, market is never null, even empty planets have markets.
//
//            //note: I'm hoping that this takes care of all connected stations.
//            //note: It does!
//        for (SectorEntityToken entity : market.getConnectedEntities()) {
//            system.removeEntity(entity);
//            //note: force deciv, should remove market for real
//            //note: full destroy, full destroy withIntel
//        }
//        DecivTracker decivtracker=DecivTracker.getInstance(); //note: wait, this is a static object I think? I shouldn't need to do this???
//        decivtracker.decivilize(market, true, true);
//
//
//        //note: todo: remove rings and coronas from planet
//
//        //note: todo remove nascent gravity well
//
//        //note: spawn debris around planet
//                //note: In retrospect I could have also used the magicCampaign function for exactly this.
//
//        MiscellaneousThemeGenerator MiscGen = new MiscellaneousThemeGenerator(); //note: The generator doesn't *seem* to be static, though the methods are?
//        BaseThemeGenerator.StarSystemData sysData = MiscGen.computeSystemData(system);
//            //note: debris field works fine, just doesn't return much in the way of resources
//            //note: todo add make debris radius dependent on the kind of target, currently it's a constant.
//            //note: todo make debris field rewards dependent on planet. eg. add some volatiles for a gas/cryo planet
//        //MiscGen.addDebrisField(sysData, planet, PLANET_DEBRIS_RADIUS);
//
//        //note: todo spawn asteroid field around planet
//
//        //note: destroy planet
//
//        system.removeEntity(planet);
//
//        //note: todo make the explosion effect that happens when a battle is won. I have no idea where to do this.
//
//
//
//        //note: sanity check that planet is gone
//
//        if (planet==null)
//        {
//            return true;
//        }
//        else{
//
//            return false; //note: wait, does returning false throw an exception anywhere, or
//        }
//
//
//    }
//}
//
//
