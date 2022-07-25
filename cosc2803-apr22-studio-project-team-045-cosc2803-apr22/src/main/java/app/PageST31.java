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
public class PageST31 implements Handler {

    // URL of this page relative to http://localhost:7001/
    public static final String URL = "/page5.html";

    // Name of the Thymeleaf HTML template page in the resources folder
    private static final String TEMPLATE = ("PageST31.html");

    @Override
    public void handle(Context context) throws Exception {
        // The model of data to provide to Thymeleaf.
        // In this example the model will remain empty
        Map<String, Object> model = new HashMap<String, Object>();

        //put into LGA into dropdown
        ArrayList<String> LGA = new ArrayList<String>();
        JDBCConnection jdbc = new JDBCConnection();
        ArrayList<LGA> lgas2018 = jdbc.getLGAs();

        LGA.add("All");

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
        Sex.add("Female");
        Sex.add("Male");
        model.put("Sex", Sex);

        // put age groups into min dropdown 

        ArrayList<String> Age_min = new ArrayList<String>();
        Age_min.add("All");
        Age_min.add("0 - 9");
        Age_min.add("10 - 19");
        Age_min.add("20 - 29");
        Age_min.add("30 - 39");
        Age_min.add("40 - 49");
        Age_min.add("50 - 59");
        Age_min.add("60+");
        model.put("Age_min", Age_min);

        // put age groups into max dropdown

       // ArrayList<String> Age_max = new ArrayList<String>();
        //Age_max.add("max");
        //Age_max.add("10");
        //Age_max.add("19");
        //Age_max.add("29");
        //Age_max.add("39");
        //Age_max.add("49");
        //Age_max.add("59");
        //model.put("Age_max", Age_max);

        // make array for value range
        ArrayList<String> value_range = new ArrayList<String>();
        int i;
        int j = 100;
        String temp;
        for (i = 0; i<40 ; ++i) {
            temp = String.valueOf(j);
            value_range.add(temp);
            j = j+100;
        }
        model.put("value_range", value_range);


        // make array for rent range
        ArrayList<String> rent_range = new ArrayList<String>();
        rent_range.add("100");
        rent_range.add("200");
        rent_range.add("300");
        rent_range.add("400");
        rent_range.add("500");
        rent_range.add("600");
        rent_range.add("700");
        rent_range.add("800");
        rent_range.add("900");
        rent_range.add("1000");
        model.put("rent_range", rent_range);

        // get inputs for basic options

        String ST31_lga = context.formParam("ST31_lga");
        String ST31_state = context.formParam("ST31_state");
        String ST31_sex = context.formParam("ST31_sex");
        String ST31_agelower = context.formParam("ST31_agelower");
        String ST31_ageupper = context.formParam("ST31_ageupper");

        //print basic group summary info

        String summary_basic = (ST31_lga + " " + ST31_state + " " + ST31_sex + " " + ST31_agelower + " " + ST31_ageupper);
        model.put("summary_basic", summary_basic);

        // Add contents to lga household information
        String region = "All";

        if (ST31_state == null || ST31_state.equals("All")) {
            region = "All";
        }
        else {
            if (ST31_state.equals("NSW")) {
                region = "New South Wales";
            }
            if (ST31_state.equals("VIC")) {
                region = "Victoria";
            }
            if (ST31_state.equals("QLD")) {
                region = "Queensland";
            }
            if (ST31_state.equals("SA")) {
                region = "South Australia";
            }
            if (ST31_state.equals("WA")) {
                region = "Western Australia";
            }
            if (ST31_state.equals("TAS")) {
                region = "Tasmania";
            }
            if (ST31_state.equals("NT")) {
                region = "Northern Territory";
            }
            if (ST31_state.equals("ACT")) {
                region = "Australian Capital Territory";
            }
        }

       
       
        ArrayList<Integer> population_state = jdbc.getPop_state(ST31_state);
        ArrayList<Integer> total_lga_population = jdbc.getLGAPopulation(ST31_lga, "2018");

        if (ST31_lga == null || ST31_lga.equals("All")) {
            model.put("region", region);
            model.put("printPop", population_state.get(0));
        } 
        else {
            model.put("region", ST31_lga);
            model.put("printPop", total_lga_population.get(0));
        }
        
        model.put("state", region);

       

        // get highest lga

        ArrayList<String> lga_highest = jdbc.UpperStateLGA(ST31_state);
        model.put("lga_highest", lga_highest.get(0));

        // get median_values
        ArrayList<Integer> median_values = jdbc.medianValues(ST31_state, ST31_lga);
        model.put("median_ageState", median_values.get(0));
        model.put("median_mortgageState", "$" + median_values.get(1));
        model.put("median_rentState", "$" + median_values.get(2));
        model.put("median_incomeState", "$" + median_values.get(3));

        // get age group 
        //int age_max = Integer.parseInt(ST31_ageupper);
       // int age_min = Integer.parseInt(ST31_agelower);

       // Give information to main function
       ArrayList<Integer> main_function = jdbc.ST31_stats(ST31_state, ST31_lga, ST31_sex, ST31_agelower);
       //get lga_population
       int lga_population = main_function.get(0);
       //get homeless population
       int homeless_population = main_function.get(1);
       // calculate and print ratio
       int ratio_num;
       if (homeless_population == 0) {
        ratio_num = 0;
       } 
       else {
        ratio_num = lga_population/homeless_population;
       }
       model.put("lga", ST31_lga);
       if (ratio_num > 0) {
        model.put("ratio_num", "1:" + ratio_num);
       } 
       else {
        model.put("ratio_num", "Undefined Data");
       }

       //get inputs for advanced options

       String ST31_lowincome = context.formParam("ST31_lowincome");
       String ST31_highincome = context.formParam("ST31_highincome");
       String ST31_lowmort = context.formParam("ST31_lowmort");
       String ST31_highmort = context.formParam("ST31_highmort");
       String ST31_lowrent = context.formParam("ST31_lowrent");
       String ST31_highrent = context.formParam("ST31_highrent");

       //define null values

       if (ST31_lowincome == null) {
        ST31_lowincome = "min";
        ST31_highincome = "max";
        ST31_lowmort = "min";
        ST31_highmort = "max";
        ST31_lowrent = "min";
        ST31_highrent = "max";
       }
     
       //get group summary info
       model.put("weekly_income",  ST31_lowincome + " - " +  ST31_highincome);
       model.put("monthly_mortgage",  ST31_lowmort + " - " +  ST31_highmort);
       model.put("weekly_rent",  ST31_lowrent + " - " +  ST31_highrent);
       model.put("sex",  ST31_sex);
       model.put("age_group",  ST31_agelower);

       // make sex values readable for query
       String readable_sex = ST31_sex;

       if (readable_sex == null) {
        readable_sex = "All";
       }
       else if (readable_sex.equals("Female")) {
        readable_sex = "f";
       }
       else if (readable_sex.equals("Male")) {
        readable_sex = "m";
       }

       // make age group values readable for query

       String readable_age = ST31_agelower;

       if (readable_age == null) {
        readable_age = "All";
       }
       else if (readable_age.equals("0 - 9")) {
        readable_age = "_0_9";
       }
       else if (readable_age.equals("10 - 19")) {
        readable_age = "_10_19";
       }
       else if (readable_age.equals("20 - 29")) {
        readable_age = "_20_29";
       }
       else if (readable_age.equals("30 - 39")) {
        readable_age = "_30_39";
       }
       else if (readable_age.equals("40 - 49")) {
        readable_age = "_40_49";
       }
       else if (readable_age.equals("50 - 59")) {
        readable_age = "_50_59";
       }
       else if (readable_age.equals("60+")) {
        readable_age = "_60_plus";
       }


       // get population of LGAs that meet the median criteria and state
       ArrayList<Integer> groupLGA_population = jdbc.filtered_LGA(ST31_state, ST31_lowincome, ST31_highincome, ST31_lowmort, ST31_highmort,  ST31_lowrent, ST31_highrent, readable_age, readable_sex );
       // give to group summary info
       model.put("groupLGA_population",  groupLGA_population.get(0));
       model.put("specified_numLGA",  groupLGA_population.get(1));
       model.put("homelessGroup_population",  groupLGA_population.get(2));

       // find homeless to population group ratio
       int group_ratio;
       if (groupLGA_population.get(2) == 0) {
        group_ratio = 0;
       } 
       else {
        group_ratio = groupLGA_population.get(0)/groupLGA_population.get(2);
       }
      
      
       if (group_ratio > 0) {
        model.put("group_ratio", "1:" + group_ratio);
       } 
       else {
        model.put("group_ratio", "Undefined Data");
       }

       // get sort
       String ST31_order = context.formParam("ST31_order");
       String ST31_median_sort = context.formParam("ST31_mediansort");

       if (ST31_order == null) {
        ST31_order = "asc";
       }
       else if (ST31_order.equals("Ascending")) {
        ST31_order = "asc";
       }
       else if (ST31_order.equals("Descending")) {
        ST31_order = "desc";
       }

       String median_sorting = " median income:";

       if (ST31_median_sort == null) {
        ST31_median_sort = "median_household_weekly_income";
        median_sorting = " medain income:";
       }
       else if (ST31_median_sort.equals("Income")) {
        ST31_median_sort = "median_household_weekly_income";
        median_sorting = " median income:";
       }
       else if ( ST31_median_sort.equals("Age")) {
        ST31_median_sort = "median_age";
        median_sorting = " median age:";
       }
       else if (ST31_median_sort.equals("Rent")) {
        ST31_median_sort = "median_rent_weekly";
        median_sorting = " median rent:";
       }
       else if ( ST31_median_sort.equals("Mortgage")) {
        ST31_median_sort = "median_rent_weekly";
        median_sorting = " median mortgage:";
       }

       // input into ranked lga function
       ArrayList<String> ranked_lga = jdbc.ranked_LGA(ST31_state, ST31_lowincome, ST31_highincome, ST31_lowmort, ST31_highmort,  ST31_lowrent, ST31_highrent, readable_age, readable_sex,  ST31_median_sort, ST31_order);


       //print sort value
       String sort = "Lowest";

       if (ST31_order.equals("asc")) {
        sort = "Lowest";
       }
       else if (ST31_order.equals("desc")) {
        sort = "Highest";
       } 

       //give order to html file
       model.put("order", sort);
       model.put("median_sorting", median_sorting);
       model.put("sorted_lga", ranked_lga.get(0) );

       //give sorted lga to function
       String sorted_lga = ranked_lga.get(0);

       ArrayList<Integer> sorted_function = jdbc.ST31_stats(ST31_state, sorted_lga, ST31_sex, ST31_agelower);
       //get lga_population
       int sorted_lga_population = sorted_function.get(0);
       //get homeless population
       int sorted_homeless_population = sorted_function.get(1);
       // calculate and print ratio
       int sorted_ratio_num;
       if (sorted_homeless_population == 0) {
        sorted_ratio_num = 0;
       } 
       else {
        sorted_ratio_num = sorted_lga_population/sorted_homeless_population;
       }

       if (sorted_ratio_num > 0) {
        model.put("sorted_ratio_num", "1:" + sorted_ratio_num);
       } 
       else {
        model.put("sorted_ratio_num",  sorted_homeless_population + ":" + sorted_lga_population);
       }




       
      


       
       
       









        // DO NOT MODIFY THIS
        // Makes Javalin render the webpage using Thymeleaf
        context.render(TEMPLATE, model);
    }

}
// package app;

// import java.util.ArrayList;

// import io.javalin.http.Context;
// import io.javalin.http.Handler;

// /**
//  * Example Index HTML class using Javalin
//  * <p>
//  * Generate a static HTML page using Javalin
//  * by writing the raw HTML into a Java String object
//  *
//  * @author Timothy Wiley, 2021. email: timothy.wiley@rmit.edu.au
//  * @author Santha Sumanasekara, 2021. email: santha.sumanasekara@rmit.edu.au
//  */
// public class PageST31 implements Handler {

//     // URL of this page relative to http://localhost:7001/
//     public static final String URL = "/page5.html";

//     @Override
//     public void handle(Context context) throws Exception {
//         // Create a simple HTML webpage in a String
//         String html = "<html>";

//         // Add some Head information
//         html = html + "<head>" + 
//                "<title>Subtask 3.1</title>";

//         // Add some CSS (external file)
//         html = html + "<link rel='stylesheet' type='text/css' href='common.css' />";
//         html = html + "</head>";

//         // Add the body
//         html = html + "<body>";

//         // Add header content block
//         html = html + """
//             <div class='header'>
//                 <h1>
//                 <a href='/'><img src='homelesslogo.png' class='top-image' alt='RMIT logo' height='50'></a>   
//                 </h1>
//             </div>
//         """;
//         // Add the topnav
//         // This uses a Java v15+ Text Block
//         html = html + """
//             <div class='topnav'>
//                 <a href='/'>Home</a>
//                 <a href='mission.html'>Our Mission</a>
//                 <a href='page3.html'>LGA Statistics</a>
//                 <a href='page5.html'>Advanced Statistics</a>
//                 <a href='page6.html'>Trends</a>
//             </div>
//         """;

//         // Add Div for page Content
//         html = html + "<div class='content'>";

//         // Look up some information from JDBC
//         // First we need to use your JDBCConnection class
//         JDBCConnection jdbc = new JDBCConnection();

//         // Next we will ask this *class* for the LGAs
//         ArrayList<LGA> lgas = jdbc.getLGAs();

//         // Add HTML for the LGA list
//         html = html + "<h1>All LGAs</h1>" + "<ul>";

//         // Finally we can print out all of the LGAs
//         for (LGA lga : lgas) {
//             html = html + "<li>" + lga.getCode16()
//                         + " - " + lga.getName16() + "</li>";
//         }

//         // Finish the List HTML
//         html = html + "</ul>";

//         // Close Content div
//         html = html + "</div>";

//         // Footer
//         html = html + """
//             <div class='footer'>
//                 <p>COSC2803 - Studio Project Starter Code</p>
//             </div>
//         """;

//         // Finish the HTML webpage
//         html = html + "</body>" + "</html>";
        

//         // DO NOT MODIFY THIS
//         // Makes Javalin render the webpage
//         context.html(html);
//     }

// }
