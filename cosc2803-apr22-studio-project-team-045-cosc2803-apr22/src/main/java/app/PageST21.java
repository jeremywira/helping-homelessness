package app;

import java.io.ObjectInputFilter.Status;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.jetty.websocket.server.WebSocketHandler.Simple;

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
public class PageST21 implements Handler {

    // URL of this page relative to http://localhost:7001/
    public static final String URL = "/page3.html";

    // Name of the Thymeleaf HTML template page in the resources folder
    private static final String TEMPLATE = ("PageST21.html");

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

        // get inputs for basic options

        String lgaSelect = context.formParam("lga_select");
        //String stateSelect = context.formParam("state_select");
        String sexSelect = context.formParam("sex_select");
        String ageSelect = context.formParam("age_select");
        String statusSelect = context.formParam("status_select");
        String yearSelect = context.formParam("year_select");
        String areaSelect = context.formParam("area_select");
        String displaySelect = context.formParam("display_select");

        ArrayList<Integer> simpleStats = jdbc.getSimpleStats(lgaSelect, sexSelect, ageSelect, yearSelect, statusSelect, displaySelect);
        ArrayList<Integer> atRisk = jdbc.getSimpleStats(lgaSelect, sexSelect, ageSelect, yearSelect, "homeless", displaySelect);
        ArrayList<Integer> homeless = jdbc.getSimpleStats(lgaSelect, sexSelect, ageSelect, yearSelect, "at_risk", displaySelect);
        ArrayList<Integer> lgaPopulationSelect = jdbc.getLGAPopulation(lgaSelect, yearSelect);
        ArrayList<String> ageSex = jdbc.getAgeSex(lgaSelect, yearSelect);
        ArrayList<Integer> lgaArea = jdbc.getLGAArea(lgaSelect);
        float dividor = simpleStats.get(0);
        float dividor2 = lgaPopulationSelect.get(0);
        float percent = ((dividor/dividor2)*100);

        if (ageSex.isEmpty()) {
            ageSex.add("Please enter");
            model.put("agedBetween", "");
            model.put("LGAName", "Please select an LGA for statistics to show");
            percent = 0;
        } else {
            model.put("age1", ageSex.get(0));
            model.put("sex1", ageSex.get(1));
            model.put("age2", ageSex.get(2));
            model.put("sex2", ageSex.get(3));
            model.put("age3", ageSex.get(4));
            model.put("sex3", ageSex.get(5));
            model.put("age4", ageSex.get(6));
            model.put("sex4", ageSex.get(7));
            model.put("age5", ageSex.get(8));
            model.put("sex5", ageSex.get(9));
            model.put("agedBetween", "s aged between ");
        }


        model.put("simpleStatsSelect", simpleStats.get(0));
        model.put("atRiskSelect", atRisk.get(0));
        model.put("homelessSelect", homeless.get(0));
        model.put("simpleStatsPercent", percent);
        model.put("LGAName", lgaSelect);
        model.put("LGAPopulation", Math.round(dividor2));
        model.put("areaSelected", "LGA");
        if (lgaArea.isEmpty()) {
            lgaArea.add(0);
        } else {
        model.put("LGAArea", lgaArea.get(0) + " km\u00B2");
        }

        ArrayList<String> stateSelect = jdbc.getLGAState(lgaSelect);
        String selectedState = "";
        if (stateSelect.isEmpty()) {
            stateSelect.add("NA");
        }
        else {
        selectedState = stateSelect.get(0);
        }
        model.put("stateName", selectedState);
        if (areaSelect == null) {
            areaSelect = "LGA";
        }
        if (areaSelect.equals("State")) {
            ArrayList<Integer> statePopulationSelect = jdbc.getStatePopulation(selectedState, yearSelect);
            if (statePopulationSelect.isEmpty()) {
                statePopulationSelect.add(0);
            }
            float statePopulation = statePopulationSelect.get(0);
            model.put("LGAPopulation", Math.round(statePopulation));
            
            ArrayList<Integer> stateStats = jdbc.getStateStats(selectedState, sexSelect, ageSelect, yearSelect, statusSelect, areaSelect);
            ArrayList<Integer> stateAtRisk = jdbc.getStateStats(selectedState, sexSelect, ageSelect, yearSelect, "homeless", areaSelect);
            ArrayList<Integer> stateHomeless = jdbc.getStateStats(selectedState, sexSelect, ageSelect, yearSelect, "at_risk", areaSelect);
            float dividor3 = stateStats.get(0);

            model.put("LGAName", selectedState);
            percent = (dividor3 / statePopulation)*100;
            model.put("simpleStatsSelect", stateStats.get(0));
            model.put("atRiskSelect", stateAtRisk.get(0));
            model.put("homelessSelect", stateHomeless.get(0));
            model.put("areaSelected", "State");
            model.put("simpleStatsPercent", percent);
            }

            model.put("AgeSelect", ageSelect);
            model.put("SexSelect", sexSelect);
            model.put("YearSelect", yearSelect);
        

        // DO NOT MODIFY THIS
        // Makes Javalin render the webpage using Thymeleaf
        context.render(TEMPLATE, model);
    }

}