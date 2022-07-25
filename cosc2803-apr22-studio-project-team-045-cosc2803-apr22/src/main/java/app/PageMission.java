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
public class PageMission implements Handler {

    // URL of this page relative to http://localhost:7001/
    public static final String URL = "/mission.html";

    // Name of the Thymeleaf HTML template page in the resources folder
    private static final String TEMPLATE = ("PageMission.html");

    @Override
    public void handle(Context context) throws Exception {
        // The model of data to provide to Thymeleaf.
        // In this example the model will remain empty
        Map<String, Object> model = new HashMap<String, Object>();

        // DO NOT MODIFY THIS
        // Makes Javalin render the webpage using Thymeleaf
        context.render(TEMPLATE, model);
    }

}

// package app;

// //import java.util.ArrayList;

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
// public class PageMission implements Handler {

//     // URL of this page relative to http://localhost:7001/
//     public static final String URL = "/mission.html";

//     @Override
//     public void handle(Context context) throws Exception {
//         // Create a simple HTML webpage in a String
//         String html = "<html>";

//         // Add some Head information
//         html = html + "<head>" + 
//                "<title>Our Mission</title>";

//         // Add some CSS (external file)
//         html = html + "<link rel='stylesheet' type='text/css' href='common.css' />";
//         html = html + "</head>";

//         // Add the body
//         html = html + "<body>";

//          // Add header content block
//         html = html + """
//              <div class='header'>
//                  <h1>
//                  <a href='/'><img src='homelesslogo.png' class='top-image' alt='RMIT logo' height='50'></a>   
//                  </h1>
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
//         html = html + "<div class='container'>";

//         // //Body Text
//         html = html + """
//             <br>
//             <img src='Helping_the_homeless.jpeg' class='img' alt='RMIT logo' height='350'>
//             <h1>Our Mission</h1>
//             <div class='aboutus'>
//                 <body>
//                 <p> To address the social challenge of Homelessness in Australia, we will need to 
//                 acknowledge that it is a problem and try to bring awareness to this issue in our society. 
//                 <br>
//                 <br>
//                 By creating this website, we are actively striving to break the stigma surrounding 
//                 homelessness and to bring this issue into the public eye. We would like to achieve 
//                 this by allowing the user to easily access data & statistics in multiple forms such 
//                 as graphs and charts that are simple to understand. 
//                 <br>
//                 <br>
//                 We would like numerous users to 
//                 be able to access and understand our website, but particularly those who are looking to 
//                 learn more about homelessness and have a greater understanding of how big the issue 
//                 has become in Australia. We would also like to provide a range of statistics for 
//                 those who are studying and going in depth about the issue to provide information 
//                 to the public. 
//                 </p></body>
//             </div>
//         """;

//         // Finish the List HTML
//         html = html + "</ul>";


//         // Close Content div
//         html = html + "</div>";

//         // Footer
//         html = html + """
//             <div class='footer'>
//                 <p>More information & links here
//                 <br>
//                 Contact Us</p>
//             </div>
//         """;

//         // Finish the HTML webpage
//         html = html + "</body>" + "</html>";
        

//         // DO NOT MODIFY THIS
//         // Makes Javalin render the webpage
//         context.html(html);
//     }

// }
