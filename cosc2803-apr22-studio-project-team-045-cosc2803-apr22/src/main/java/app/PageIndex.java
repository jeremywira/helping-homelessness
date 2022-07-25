package app;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.jetty.client.api.Request;

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
public class PageIndex implements Handler {  
    // URL of this page relative to http://localhost:7001/
    public static final String URL = "/";

    // Name of the Thymeleaf HTML template page in the resources folder
    private static final String TEMPLATE = ("PageIndex.html");

    @Override
    public void handle(Context context) throws Exception {
        // The model of data to provide to Thymeleaf.
        // In this example the model will remain empty
        Map<String, Object> model = new HashMap<String, Object>();

        ArrayList<String> year = new ArrayList<String>();
        year.add("2016");
        year.add("2018");
        model.put("year", year);

        JDBCConnection jdbc = new JDBCConnection();
        ArrayList<LGA> lgas2018 = jdbc.getLGAs();
        ArrayList<String> lga_print2018= new ArrayList<String>();
        Integer lga_total = 0;

        for (LGA lga : lgas2018) {
            ++lga_total; 
            lga_print2018.add(lga.getName16());
        }


        ArrayList<Integer> pop_2016 = jdbc.getPopulation2016();
        ArrayList<Integer> pop_2018 = jdbc.getPopulation2018();

        String year_select = context.formParam("year_select");
        String year_chosen = "Australia's Population in 2016:";

        if (year_select == null) {
            model.put("population_index", (pop_2016.get(0)) )        ;
            model.put("lga_index", (lga_total));
            model.put("year_chosen", (year_chosen));
        } 
        else {
            if (year_select.equals("2016")) {
                model.put("population_index", (pop_2016.get(0)));
                model.put("lga_index", (lga_total));
                year_chosen = "Australia's Population in " + year_select + ":";
                model.put("year_chosen", (year_chosen));
            }
            if (year_select.equals("2018")) {
                model.put("population_index", (pop_2018.get(0)));
                model.put("lga_index", (lga_total));
                year_chosen = "Australia's Population in " + year_select + ":";
                model.put("year_chosen", (year_chosen));
            }
        }

        // DO NOT MODIFY THIS
        // Makes Javalin render the webpage using Thymeleaf
        context.render(TEMPLATE, model);
    }

}
