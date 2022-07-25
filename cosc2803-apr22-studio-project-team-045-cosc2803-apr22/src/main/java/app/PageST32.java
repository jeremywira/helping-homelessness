package app;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import io.javalin.http.Context;
import io.javalin.http.Handler;

/**
 * Example Index HTML class using Javalin
 * <p>
 * Generate a static HTML page using Javalin
 * by writing the raw HTML into a Java String object
 *
 * @author Timothy Wiley, 2021. email: timothy.wiley@rmit.edu.au
 * @author Santha Sumanasekara, 2021. email: santha.sumanasekara@rmit.edu.au
 */
public class PageST32 implements Handler {

    // URL of this page relative to http://localhost:7001/
    public static final String URL = "/page6.html";

    // Name of the Thymeleaf HTML template page in the resources folder
    private static final String TEMPLATE = ("PageST32.html");

    @Override
    public void handle(Context context) throws Exception {
        // The model of data to provide to Thymeleaf.
        // In this example the model will remain empty
        Map<String, Object> model = new HashMap<String, Object>();

        JDBCConnection jdbc = new JDBCConnection();
        
        ArrayList<LGA> lgas2018 = jdbc.getLGAs();
        ArrayList<String> LGA = new ArrayList<String>();

        for (LGA lga : lgas2018) {
            LGA.add(lga.getName16());
        }

        model.put("LGA", LGA);

        //put state into dropdown

        ArrayList<String> States = new ArrayList<String>();
        States.add("All");
        States.add("NT");
        States.add("NSW");
        States.add("QLD");
        States.add("SA");
        States.add("TAS");
        States.add("VIC");
        States.add("WA");
        model.put("States", States);

        // put sex into dropdown

        ArrayList<String> Sex = new ArrayList<String>();
        Sex.add("All");
        Sex.add("Male");
        Sex.add("Female");
        model.put("Sex", Sex);

        // put age groups into min dropdown 

        ArrayList<String> Age = new ArrayList<String>();
        Age.add("All");
        Age.add("0 - 9");
        Age.add("10 - 19");
        Age.add("20 - 29");
        Age.add("30 - 39");
        Age.add("40 - 49");
        Age.add("50 - 59");
        Age.add("60+");
        model.put("Age", Age);

        ArrayList<String> Status = new ArrayList<String>();
        Status.add("All");
        Status.add("Homeless");
        Status.add("At Risk");
        model.put("Status", Status);

        ArrayList<Integer> Year = new ArrayList<Integer>();
        Year.add(2016);
        Year.add(2018);
        model.put("Year", Year);

        ArrayList<String> Area = new ArrayList<String>();
        Area.add("LGA");
        Area.add("State");
        Area.add("Nation");
        model.put("Area", Area);

        ArrayList<String> Display = new ArrayList<String>();
        Display.add("Total");
        Display.add("Percent");
        model.put("Display", Display);

        ArrayList<String> BestWorst = new ArrayList<String>();
        BestWorst.add("None");
        BestWorst.add("Largest Increase");
        BestWorst.add("Largest Decrease");
        BestWorst.add("Smallest Increase");
        BestWorst.add("Smallest Decrease");
        model.put("BestWorst", BestWorst);


        // get inputs for basic options

        String lgaSelect = context.formParam("lga_select");
        //String stateSelect = context.formParam("state_select");
        String sexSelect = context.formParam("sex_select");
        String ageSelect = context.formParam("age_select");
        String statusSelect = context.formParam("status_select");
        String areaSelect = context.formParam("area_select");
        String displaySelect = context.formParam("display_select");
        String bestWorstSelect = context.formParam("bestWorstSelect");

        if (bestWorstSelect != "None") {
            
        }



        ArrayList<Integer> total2016 = jdbc.getSimpleStats(lgaSelect, sexSelect, ageSelect, "2016", "", displaySelect);
        ArrayList<Integer> total2018 = jdbc.getSimpleStats(lgaSelect, sexSelect, ageSelect, "2018", "", displaySelect);

        ArrayList<Integer> lgaHomeless2016 = jdbc.getSimpleStats(lgaSelect, sexSelect, ageSelect, "2016", "Homeless", areaSelect);
        ArrayList<Integer> lgaHomeless2018 = jdbc.getSimpleStats(lgaSelect, sexSelect, ageSelect, "2018", "Homeless", areaSelect);

        ArrayList<Integer> lgaAtRisk2016 = jdbc.getSimpleStats(lgaSelect, sexSelect, ageSelect, "2016", "At Risk", areaSelect);
        ArrayList<Integer> lgaAtRisk2018 = jdbc.getSimpleStats(lgaSelect, sexSelect, ageSelect, "2018", "At Risk", areaSelect);

        float tot2016 = total2016.get(0);
        float tot2018 = total2018.get(0);
        float totChange = tot2018 - tot2016;
        float pctChange = (totChange / tot2016)*100;

        float homeless2016LGA = lgaHomeless2016.get(0);
        float homeless2018LGA = lgaHomeless2018.get(0);
        float homelessChangeLGA = homeless2018LGA - homeless2016LGA;
        float pctHomelessChangeLGA = (homelessChangeLGA / homeless2016LGA)*100;

        float atRisk2016LGA = lgaAtRisk2016.get(0);
        float atRisk2018LGA = lgaAtRisk2018.get(0);
        float atRiskChangeLGA = atRisk2018LGA - atRisk2016LGA;
        float pctAtRiskChangeLGA = (atRiskChangeLGA / atRisk2016LGA)*100;

        if (lgaSelect == null) {
            pctChange = 0;
            pctHomelessChangeLGA = 0;
            pctAtRiskChangeLGA = 0;
        }

        model.put("total2016", Math.round(tot2016));
        model.put("total2018", Math.round(tot2018));
        model.put("changeTotal", Math.round(totChange));
        model.put("changePct", pctChange);
        model.put("LGAName", lgaSelect);

        model.put("homeless2016", Math.round(homeless2016LGA));
        model.put("homeless2018", Math.round(homeless2018LGA));
        model.put("changeHomeless", Math.round(homelessChangeLGA));
        model.put("changeHomelessPct", pctHomelessChangeLGA);

        model.put("atRisk2016", Math.round(atRisk2016LGA));
        model.put("atRisk2018", Math.round(atRisk2018LGA));
        model.put("changeAtRisk", Math.round(atRiskChangeLGA));
        model.put("changeAtRiskPct", pctAtRiskChangeLGA);

        ArrayList<String> stateSelect = jdbc.getLGAState(lgaSelect);
        if (stateSelect.isEmpty()) {
            stateSelect.add("");
        }
        String selectedState = stateSelect.get(0);
        model.put("stateName", selectedState);

        if (areaSelect == null) {
            areaSelect = "LGA";
        }

        if (areaSelect.equals("State")) {
            ArrayList<Integer> stateTotal2016 = jdbc.getStateStats(selectedState, sexSelect, ageSelect, "2016", null, areaSelect);
            ArrayList<Integer> stateTotal2018 = jdbc.getStateStats(selectedState, sexSelect, ageSelect, "2018", null, areaSelect);

            ArrayList<Integer> stateHomeless2016 = jdbc.getStateStats(selectedState, sexSelect, ageSelect, "2016", "Homeless", areaSelect);
            ArrayList<Integer> stateHomeless2018 = jdbc.getStateStats(selectedState, sexSelect, ageSelect, "2018", "Homeless", areaSelect);

            ArrayList<Integer> stateAtRisk2016 = jdbc.getStateStats(selectedState, sexSelect, ageSelect, "2016", "At Risk", areaSelect);
            ArrayList<Integer> stateAtRisk2018 = jdbc.getStateStats(selectedState, sexSelect, ageSelect, "2018", "At Risk", areaSelect);

            float tot2016State = stateTotal2016.get(0);
            float tot2018State = stateTotal2018.get(0);
            float totChangeState = tot2018State - tot2016State;
            float pctChangeState = (totChangeState / tot2016State)*100;

            float homeless2016State = stateHomeless2016.get(0);
            float homeless2018State = stateHomeless2018.get(0);
            float homelessChangeState = homeless2018State - homeless2016State;
            float pctHomelessChangeState = (homelessChangeState / homeless2016State)*100;

            float atRisk2016State = stateAtRisk2016.get(0);
            float atRisk2018State = stateAtRisk2018.get(0);
            float atRiskChangeState = atRisk2018State - atRisk2016State;
            float pctAtRiskChangeState = (atRiskChangeState / atRisk2016State)*100;
    
            if (lgaSelect == null) {
                pctChange = 0;
            }
    
            model.put("total2016", Math.round(tot2016State));
            model.put("total2018", Math.round(tot2018State));
            model.put("changeTotal", Math.round(totChangeState));
            model.put("changePct", pctChangeState);
            model.put("LGAName", selectedState);

            model.put("homeless2016", Math.round(homeless2016State));
            model.put("homeless2018", Math.round(homeless2018State));
            model.put("changeHomeless", Math.round(homelessChangeState));
            model.put("changeHomelessPct", pctHomelessChangeState);

            model.put("atRisk2016", Math.round(atRisk2016State));
            model.put("atRisk2018", Math.round(atRisk2018State));
            model.put("changeAtRisk", Math.round(atRiskChangeState));
            model.put("changeAtRiskPct", pctAtRiskChangeState);

        }

        // DO NOT MODIFY THIS
        // Makes Javalin render the webpage using Thymeleaf
        context.render(TEMPLATE, model);
    }

}

