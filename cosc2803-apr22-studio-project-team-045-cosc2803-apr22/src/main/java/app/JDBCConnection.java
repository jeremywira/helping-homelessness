package app;

import java.util.ArrayList;

import org.eclipse.jetty.websocket.server.WebSocketHandler.Simple;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Class for Managing the JDBC Connection to a SQLLite Database.
 * Allows SQL queries to be used with the SQLLite Databse in Java.
 *
 * @author Timothy Wiley, 2022. email: timothy.wiley@rmit.edu.au
 * @author Santha Sumanasekara, 2021. email: santha.sumanasekara@rmit.edu.au
 */
public class JDBCConnection {

    // Name of database file (contained in database folder)
    private static final String DATABASE = "jdbc:sqlite:database/homelessness.db";

    // If you are using the Homelessness data set replace this with the below
    //private static final String DATABASE = "jdbc:sqlite:database/homelessness.db";

    /**
     * This creates a JDBC Object so we can keep talking to the database
     */
    public JDBCConnection() {
        System.out.println("Created JDBC Connection Object");
    }

    /**
     * Get all of the LGAs in the database.
     * @return
     *    Returns an ArrayList of LGA objects
     */
    public ArrayList<LGA> getLGAs() {
        // Create the ArrayList of LGA objects to return
        ArrayList<LGA> lgas = new ArrayList<LGA>();

        // Setup the variable for the JDBC connection
        Connection connection = null;

        try {
            // Connect to JDBC data base
            connection = DriverManager.getConnection(DATABASE);

            // Prepare a new SQL Query & Set a timeout
            Statement statement = connection.createStatement();
            statement.setQueryTimeout(30);

            // The Query
            String query = "SELECT * FROM LGA";
            
            // Get Result
            ResultSet results = statement.executeQuery(query);

            // Process all of the results
            while (results.next()) {
                // Lookup the columns we need
                int code16     = results.getInt("lga_code");
                String name16  = results.getString("lga_name");

                // Create a LGA Object
                LGA lga = new LGA(code16, name16);

                // Add the lga object to the array
                lgas.add(lga);
            }

            // Close the statement because we are done with it
            statement.close();
        } catch (SQLException e) {
            // If there is an error, lets just pring the error
            System.err.println(e.getMessage());
        } finally {
            // Safety code to cleanup
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                // connection close failed.
                System.err.println(e.getMessage());
            }
        }

        // Finally we return all of the lga
        return lgas;
    }

    // TODO: Add your required methods here

    public ArrayList<Integer> getSimpleStats(String lgaSelect, String sexSelect, String ageSelect, String yearSelect, String statusSelect, String displaySelect) {
        // Create the ArrayList of LGA objects to return
        ArrayList<Integer> SimpleStat = new ArrayList<Integer>();

        // Setup the variable for the JDBC connection
        Connection connection = null;

        try {
            // Connect to JDBC data base
            connection = DriverManager.getConnection(DATABASE);

            // Prepare a new SQL Query & Set a timeout
            Statement statement = connection.createStatement();
            statement.setQueryTimeout(30);

            if (sexSelect == null) {
                sexSelect = "All";
            }
            else if (sexSelect.equals("Male")) {
                sexSelect = "m";
            } else if (sexSelect.equals("Female")) {
                sexSelect = "f";
            }

             var gotStatusSelect = true; 
             if (statusSelect == null || statusSelect == "") {
                gotStatusSelect = false;
             }
             
             if (gotStatusSelect) {
             if (statusSelect.equals("Homeless")) {
                statusSelect = "homeless";
             }
             if (statusSelect.equals("At Risk")) {
                statusSelect = "at_risk";
             }
            }

            if (ageSelect == null) {
            ageSelect = "All";
             }
             else if (ageSelect.equals("0 - 9")) {
                ageSelect = "_0_9";
             } else if (ageSelect.equals("10 - 19")) {
                ageSelect = "_10_19";
             } else if (ageSelect.equals("20 - 29")) {
                ageSelect = "_20_29";
             } else if (ageSelect.equals("30 - 39")) {
                ageSelect = "_30_39";
             } else if (ageSelect.equals("40 - 49")) {
                ageSelect = "_40_49";
             } else if (ageSelect.equals("50 - 59")) {
                ageSelect = "_50_59";
             } else if (ageSelect.equals("60+")) {
                ageSelect = "_60_plus";
             }

             if (ageSelect.equals("All")) {
                ageSelect = "";
                } else {
                ageSelect = "AND age_group = '" + ageSelect + "'";
                }
            if (sexSelect.equals("All")) {
                sexSelect = "";
                } else {
                sexSelect = "AND sex = '" + sexSelect + "'";
                }

             String query;
            // The Query
            if (gotStatusSelect) {         
                query = "SELECT sum(count) FROM Homeless_atRisk h JOIN LGA l on h.lga_code = l.lga_code WHERE year = " + yearSelect + " AND l.lga_name = '" + lgaSelect + "' AND h.status = '" + statusSelect + "'" + ageSelect + sexSelect + ";";
                } else {
                query = "SELECT sum(count) FROM Homeless_atRisk h JOIN LGA l on h.lga_code = l.lga_code WHERE year = " + yearSelect + " AND l.lga_name = '" + lgaSelect + "'" + ageSelect + sexSelect + ";";
                }
            // Get Result
            ResultSet results = statement.executeQuery(query);

            // Process all of the results
            while (results.next()) {
                // Lookup the columns we need

                int stats = results.getInt("sum(count)");

                // Create a LGA Object
                // Add the lga object to the array
               SimpleStat.add(stats);
             }

            // Close the statement because we are done with it
            statement.close();
        } catch (SQLException e) {
            // If there is an error, lets just pring the error
            System.err.println(e.getMessage());
        } finally {
            // Safety code to cleanup
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                // connection close failed.
                System.err.println(e.getMessage());
            }
        }

        // Finally we return all of the lga
        return SimpleStat;
    }


    public ArrayList<String> getAgeSex(String lgaSelect, String yearSelect) {
        // Create the ArrayList of LGA objects to return
        ArrayList<String> ageSexTop = new ArrayList<String>();

        // Setup the variable for the JDBC connection
        Connection connection = null;

        if (lgaSelect == null) {
            lgaSelect = "";
        } 

        if (yearSelect == null) {
            yearSelect = "";
        }

        try {
            // Connect to JDBC data base
            connection = DriverManager.getConnection(DATABASE);

            // Prepare a new SQL Query & Set a timeout
            Statement statement = connection.createStatement();
            statement.setQueryTimeout(30);

            // The Query
            String query = "SELECT age_group, sex FROM Homeless_atRisk h JOIN LGA l on h.lga_code = l.lga_code WHERE l.lga_name = '" + lgaSelect + "' AND year = '" + yearSelect + "'ORDER by count DESC;";

            // Get Result
            ResultSet results = statement.executeQuery(query);

            // Process all of the results
            while (results.next()) {
                // Lookup the columns we need

                String ageGroup = results.getString("age_group");
                String sexGroup = results.getString("sex");
                if (sexGroup.equals("m")) {
                    sexGroup = "Male";
                } else {
                    sexGroup = "Female";
                }
                if (ageGroup.equals("_0_9")) {
                    ageGroup = "0 - 9";
                } else if (ageGroup.equals("_10_19")) {
                    ageGroup = "10 - 19";
                } else if (ageGroup.equals("_20_29")) {
                    ageGroup = "20 - 29";
                } else if (ageGroup.equals("_30_39")) {
                    ageGroup = "30 - 39";
                } else if (ageGroup.equals("_40_49")) {
                    ageGroup = "40 - 49";
                } else if (ageGroup.equals("_50_59")) {
                    ageGroup = "50 - 59";
                } else if (ageGroup.equals("_60_plus")) {
                    ageGroup = "60+";
                }

                // Create a LGA Object
                // Add the lga object to the array
                ageSexTop.add(ageGroup);
                ageSexTop.add(sexGroup);
             }

            // Close the statement because we are done with it
            statement.close();
        } catch (SQLException e) {
            // If there is an error, lets just pring the error
            System.err.println(e.getMessage());
        } finally {
            // Safety code to cleanup
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                // connection close failed.
                System.err.println(e.getMessage());
            }
        }

        // Finally we return all of the lga
        return ageSexTop;
    }

    public ArrayList<Integer> getLGAArea(String lgaSelect) {
        // Create the ArrayList of LGA objects to return
        ArrayList<Integer> LGAArea = new ArrayList<Integer>();

        // Setup the variable for the JDBC connection
        Connection connection = null;
        
        if (lgaSelect == null) {
            lgaSelect = "";
        } 

        try {
            // Connect to JDBC data base
            connection = DriverManager.getConnection(DATABASE);

            // Prepare a new SQL Query & Set a timeout
            Statement statement = connection.createStatement();
            statement.setQueryTimeout(30);

            // The Query     
            String query = "SELECT area_sqkm FROM LGA WHERE lga_name = '" + lgaSelect +"';";
            // Get Result
            ResultSet results = statement.executeQuery(query);

            // Process all of the results
            while (results.next()) {
                // Lookup the columns we need

                int area = results.getInt("area_sqkm");

                // Create a LGA Object
                // Add the lga object to the array
                LGAArea.add(area);
             }

            // Close the statement because we are done with it
            statement.close();
        } catch (SQLException e) {
            // If there is an error, lets just pring the error
            System.err.println(e.getMessage());
        } finally {
            // Safety code to cleanup
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                // connection close failed.
                System.err.println(e.getMessage());
            }
        }

        // Finally we return all of the lga
        return LGAArea;
    }

    // public ArrayList<String> getIncreaseDecrease(String bestWorstSelect) {
    //     // Create the ArrayList of LGA objects to return
    //     ArrayList<String> LGAIncreaseDecrease = new ArrayList<String>();

    //     // Setup the variable for the JDBC connection
    //     Connection connection = null;

    //     try {
    //         // Connect to JDBC data base
    //         connection = DriverManager.getConnection(DATABASE);

    //         // Prepare a new SQL Query & Set a timeout
    //         Statement statement = connection.createStatement();
    //         statement.setQueryTimeout(30);

    //         // The Query     
    //         String query = "SELECT c.lga_name, a.age_group, a.sex, a.status, (cast(b.count - a.count as float) / a.count) * 100 as PctChange FROM Homeless_atRisk a left join Homeless_atRisk b on a.lga_code = b.lga_code JOIN LGA c on a.lga_code = c.lga_code WHERE a.year != b.year AND b.year = '2018' AND a.age_group = b.age_group AND a.sex = b.sex AND PctChange is not null AND PctChange != -100 AND a.status = b.status ORDER by PctChange DESC;";
    //         // Get Result
    //         ResultSet results = statement.executeQuery(query);

    //         // Process all of the results
    //         while (results.next()) {
    //             // Lookup the columns we need

    //             String lgaName = results.getString("lga_name");
    //             String pctChange = results.getString("PctChange");
    //             String ageGroup = results.getString("age_group");
    //             String sex = results.getString("sex");
    //             String status = results.getString("status");

    //             // Create a LGA Object
    //             // Add the lga object to the array
    //             LGAIncreaseDecrease.add(pctChange);
    //             LGAIncreaseDecrease.add(lgaName);
    //             LGAIncreaseDecrease.add(ageGroup);
    //             LGAIncreaseDecrease.add(sex);
    //             LGAIncreaseDecrease.add(status);
    //          }

    //         // Close the statement because we are done with it
    //         statement.close();
    //     } catch (SQLException e) {
    //         // If there is an error, lets just pring the error
    //         System.err.println(e.getMessage());
    //     } finally {
    //         // Safety code to cleanup
    //         try {
    //             if (connection != null) {
    //                 connection.close();
    //             }
    //         } catch (SQLException e) {
    //             // connection close failed.
    //             System.err.println(e.getMessage());
    //         }
    //     }

    //     // Finally we return all of the lga
    //     return changePct;
    // }

    public ArrayList<Integer> getLGAPopulation(String lgaSelect, String yearSelect) {
        // Create the ArrayList of LGA objects to return
        ArrayList<Integer> LGAPopulation = new ArrayList<Integer>();

        // Setup the variable for the JDBC connection
        Connection connection = null;

        try {
            // Connect to JDBC data base
            connection = DriverManager.getConnection(DATABASE);

            // Prepare a new SQL Query & Set a timeout
            Statement statement = connection.createStatement();
            statement.setQueryTimeout(30);

            // The Query     
            String query = "SELECT sum(population) FROM LGAStatistics h JOIN LGA l on h.lga_code = l.lga_code WHERE year = " + yearSelect + " AND l.lga_name = '" + lgaSelect +"';";
            // Get Result
            ResultSet results = statement.executeQuery(query);

            // Process all of the results
            while (results.next()) {
                // Lookup the columns we need

                int population = results.getInt("sum(population)");

                // Create a LGA Object
                // Add the lga object to the array
                LGAPopulation.add(population);
             }

            // Close the statement because we are done with it
            statement.close();
        } catch (SQLException e) {
            // If there is an error, lets just pring the error
            System.err.println(e.getMessage());
        } finally {
            // Safety code to cleanup
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                // connection close failed.
                System.err.println(e.getMessage());
            }
        }

        // Finally we return all of the lga
        return LGAPopulation;
    }

    public ArrayList<String> getLGAState(String lgaSelect) {
        // Create the ArrayList of LGA objects to return
        ArrayList<String> LGAState = new ArrayList<String>();

        // Setup the variable for the JDBC connection
        Connection connection = null;

        try {
            // Connect to JDBC data base
            connection = DriverManager.getConnection(DATABASE);

            // Prepare a new SQL Query & Set a timeout
            Statement statement = connection.createStatement();
            statement.setQueryTimeout(30);

            // The Query     
            String query = "SELECT lga_code FROM LGA WHERE lga_name = '" + lgaSelect + "';";
            // Get Result
            ResultSet results = statement.executeQuery(query);

            // Process all of the results
            while (results.next()) {
                // Lookup the columns we need

                String lga_code = results.getString("lga_code");
                String state = ""; 
                if (lga_code.startsWith("1")) {
                    state = "NSW";
                } else if (lga_code.startsWith("2")) {
                    state = "VIC";
                } else if (lga_code.startsWith("3")) {
                    state = "QLD";
                } else if (lga_code.startsWith("4")) {
                    state = "SA";
                } else if (lga_code.startsWith("5")) {
                    state = "WA";
                } else if (lga_code.startsWith("6")) {
                    state = "TAS";
                } else if (lga_code.startsWith("7")) {
                    state = "NT";
                }

                // Create a LGA Object
                // Add the lga object to the array
                LGAState.add(state);
             }

            // Close the statement because we are done with it
            statement.close();
        } catch (SQLException e) {
            // If there is an error, lets just pring the error
            System.err.println(e.getMessage());
        } finally {
            // Safety code to cleanup
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                // connection close failed.
                System.err.println(e.getMessage());
            }
        }

        // Finally we return all of the lga
        return LGAState;
    }

    public ArrayList<Integer> getStatePopulation(String selectedState, String yearSelect) {
        // Create the ArrayList of LGA objects to return
        ArrayList<Integer> StatePopulation = new ArrayList<Integer>();

        // Setup the variable for the JDBC connection
        Connection connection = null;

        try {
            // Connect to JDBC data base
            connection = DriverManager.getConnection(DATABASE);

            // Prepare a new SQL Query & Set a timeout
            Statement statement = connection.createStatement();
            statement.setQueryTimeout(30);
            String stateCode = "0";

            if (selectedState == null) {

            }
            if (yearSelect == null) {
                
            }

            if (selectedState.equals("NSW")) {
                stateCode = "1";
            } else if (selectedState.equals("VIC")) {
                stateCode = "2";
            }  else if (selectedState.equals("QLD")) {
                stateCode = "3";
            } else if (selectedState.equals("SA")) {
                stateCode = "4";
            } else if (selectedState.equals("WA")) {
                stateCode = "5";
            } else if (selectedState.equals("TAS")) {
                stateCode = "6";
            } else if (selectedState.equals("NT")) {
                stateCode = "7";
            }

            // The Query     
            String query = "SELECT sum(population) FROM LGAStatistics WHERE lga_code like '" + stateCode + "%' AND year = '" + yearSelect + "';";
            // Get Result
            ResultSet results = statement.executeQuery(query);

            // Process all of the results
            while (results.next()) {
                //Homelessness2018 male092018 = new Homelessness2018();
                // Lookup the columns we need

                int statePopulation = results.getInt("sum(population)");

                // Create a LGA Object
                // Add the lga object to the array
                StatePopulation.add(statePopulation);
             }

            // Close the statement because we are done with it
            statement.close();
        } catch (SQLException e) {
            // If there is an error, lets just pring the error
            System.err.println(e.getMessage());
        } finally {
            // Safety code to cleanup
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                // connection close failed.
                System.err.println(e.getMessage());
            }
        }

        // Finally we return all of the lga
        return StatePopulation;
    }

    public ArrayList<Integer> getStateStats(String selectedState, String sexSelect, String ageSelect, String yearSelect, String statusSelect, String displaySelect) {
        // Create the ArrayList of LGA objects to return
        ArrayList<Integer> StateStat = new ArrayList<Integer>();

        // Setup the variable for the JDBC connection
        Connection connection = null;

        try {
            // Connect to JDBC data base
            connection = DriverManager.getConnection(DATABASE);

            // Prepare a new SQL Query & Set a timeout
            Statement statement = connection.createStatement();
            statement.setQueryTimeout(30);
            String stateCode = "0";
            if (selectedState.equals("NSW")) {
                stateCode = "1";
            } else if (selectedState.equals("VIC")) {
                stateCode = "2";
            }  else if (selectedState.equals("QLD")) {
                stateCode = "3";
            } else if (selectedState.equals("SA")) {
                stateCode = "4";
            } else if (selectedState.equals("WA")) {
                stateCode = "5";
            } else if (selectedState.equals("TAS")) {
                stateCode = "6";
            } else if (selectedState.equals("NT")) {
                stateCode = "7";
            }

            if (sexSelect == null) {
                sexSelect = "All";
            }
            else if (sexSelect.equals("Male")) {
                sexSelect = "m";
            } else if (sexSelect.equals("Female")) {
                sexSelect = "f";
            }

             var gotStatusSelect = true; 
             if (statusSelect == null || statusSelect.equals("")) {
                gotStatusSelect = false;
             }
             if (gotStatusSelect) {
             if (statusSelect.equals("Homeless")) {
                statusSelect = "homeless";
             }
             else if (statusSelect.equals("At Risk")) {
                statusSelect = "at_risk";
             }
            }
             
             if (ageSelect == null) {
                ageSelect = "All";
             }
             else if (ageSelect.equals("0 - 9")) {
                ageSelect = "_0_9";
             } else if (ageSelect.equals("10 - 19")) {
                ageSelect = "_10_19";
             } else if (ageSelect.equals("20 - 29")) {
                ageSelect = "_20_29";
             } else if (ageSelect.equals("30 - 39")) {
                ageSelect = "_30_39";
             } else if (ageSelect.equals("40 - 49")) {
                ageSelect = "_40_49";
             } else if (ageSelect.equals("50 - 59")) {
                ageSelect = "_50_59";
             } else if (ageSelect.equals("60+")) {
                ageSelect = "_60_plus";
             }

             if (ageSelect.equals("All")) {
                ageSelect = "";
                } else {
                ageSelect = "AND age_group = '" + ageSelect + "'";
                }
            if (sexSelect.equals("All")) {
                sexSelect = "";
                } else {
                sexSelect = "AND sex = '" + sexSelect + "'";
                }
             String query;
            // The Query
            if (gotStatusSelect) {         
                query = "SELECT sum(count) FROM Homeless_atRisk h JOIN LGA l on h.lga_code = l.lga_code WHERE year = " + yearSelect + " AND l.lga_code like '" + stateCode + "%' AND status = '" + statusSelect + "'" + ageSelect + sexSelect + ";";
                } else {
                query = "SELECT sum(count) FROM Homeless_atRisk h JOIN LGA l on h.lga_code = l.lga_code WHERE year = " + yearSelect + " AND l.lga_code like '" + stateCode + "%'" + ageSelect + sexSelect + ";";
                }
            // Get Result
            ResultSet results = statement.executeQuery(query);

            // Process all of the results
            while (results.next()) {
                // Lookup the columns we need

                int stats = results.getInt("sum(count)");

                // Create a LGA Object
                // Add the lga object to the array
                StateStat.add(stats);
             }

            // Close the statement because we are done with it
            statement.close();
        } catch (SQLException e) {
            // If there is an error, lets just pring the error
            System.err.println(e.getMessage());
        } finally {
            // Safety code to cleanup
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                // connection close failed.
                System.err.println(e.getMessage());
            }
        }

        // Finally we return all of the lga
        return StateStat;
    }
 













































































        public ArrayList<Integer> getPopulation2016() {
            // Create the ArrayList of LGA objects to return
            ArrayList<Integer> population2016 = new ArrayList<Integer>();
    
            // Setup the variable for the JDBC connection
            Connection connection = null;
    
            try {
                // Connect to JDBC data base
                connection = DriverManager.getConnection(DATABASE);
    
                // Prepare a new SQL Query & Set a timeout
                Statement statement = connection.createStatement();
                statement.setQueryTimeout(30);
    
                // The Query
                String query = "select sum(population) from LGAStatistics where year = 2016;";
                
                // Get Result
                ResultSet results = statement.executeQuery(query);
    
                // Process all of the results
                while (results.next()) {
                    // Lookup the columns we need
                    int results2016     = results.getInt("sum(population)");
                    population2016.add(results2016);
                }
    
                // Close the statement because we are done with it
                statement.close();
            } catch (SQLException e) {
                // If there is an error, lets just pring the error
                System.err.println(e.getMessage());
            } finally {
                // Safety code to cleanup
                try {
                    if (connection != null) {
                        connection.close();
                    }
                } catch (SQLException e) {
                    // connection close failed.
                    System.err.println(e.getMessage());
                }
            }
    
            // Finally we return all of the lga
            return population2016;
        }

        public ArrayList<Integer> getPop_state(String state) {
            // Create the ArrayList of LGA objects to return
            ArrayList<Integer> population_state = new ArrayList<Integer>();
    
             // Setup the variable for the JDBC connection
             Connection connection = null;
    
             try {
                 // Connect to JDBC data base
                 connection = DriverManager.getConnection(DATABASE);
    
                // Prepare a new SQL Query & Set a timeout
                Statement statement = connection.createStatement();
                statement.setQueryTimeout(30);

                String state_digit = "0";

                if (state == null || state.equals("All")) {
                    state_digit = "9";
                }
                else {
                    if (state.equals("NSW")) {
                        state_digit = "1";
                    }
                    if (state.equals("VIC")) {
                        state_digit = "2";
                    }
                    if (state.equals("QLD")) {
                        state_digit = "3";
                    }
                    if (state.equals("SA")) {
                        state_digit = "4";
                    }
                    if (state.equals("WA")) {
                        state_digit = "5";
                    }
                    if (state.equals("TAS")) {
                        state_digit = "6";
                    }
                    if (state.equals("NT")) {
                        state_digit = "7";
                    }
                } 

                // The Query
                String query = ("select sum(population) from LGAStatistics where lga_code like '" + state_digit + "%' and year = 2018;");

                if (state_digit.equals("9")) {
                    query = "select sum(population) from LGAStatistics where year = 2018;";
                }

                
                 // Get Result
                 ResultSet results = statement.executeQuery(query);
    
                // Process all of the results
                while (results.next()) {
                    // Lookup the columns we need
                    int resultsfinal     = results.getInt("sum(population)");
                    population_state.add(resultsfinal);
                }
    
                 // Close the statement because we are done with it
                 statement.close();
             } catch (SQLException e) {
                 // If there is an error, lets just pring the error
                 System.err.println(e.getMessage());
             } finally {
                 // Safety code to cleanup
                 try {
                     if (connection != null) {
                         connection.close();
                     }
                 } catch (SQLException e) {
                     // connection close failed.
                     System.err.println(e.getMessage());
                 }
             }
    
            // Finally we return all of the lga
            return population_state;
        }

        public ArrayList<String> UpperStateLGA(String state) {
            // Create the ArrayList of LGA objects to return
            ArrayList<String> HighestLga = new ArrayList<String>();
    
             // Setup the variable for the JDBC connection
             Connection connection = null;
    
             try {
                 // Connect to JDBC data base
                 connection = DriverManager.getConnection(DATABASE);
    
                // Prepare a new SQL Query & Set a timeout
                Statement statement = connection.createStatement();
                statement.setQueryTimeout(30);

                String state_digit = "0";

                if (state == null || state.equals("All")) {
                    state_digit = "9";
                }
                else {
                    if (state.equals("NSW")) {
                        state_digit = "1";
                    }
                    if (state.equals("VIC")) {
                        state_digit = "2";
                    }
                    if (state.equals("QLD")) {
                        state_digit = "3";
                    }
                    if (state.equals("SA")) {
                        state_digit = "4";
                    }
                    if (state.equals("WA")) {
                        state_digit = "5";
                    }
                    if (state.equals("TAS")) {
                        state_digit = "6";
                    }
                    if (state.equals("NT")) {
                        state_digit = "7";
                    }
                } 

                // The Query
                String query = """
                select lga_name, sum(count)
                from LGAStatistics as s
                join LGA as l on l.lga_code = s.lga_code
                join Homeless_atRisk as h on l.lga_code = h.lga_code 
                where l.lga_code like '""" +
                state_digit +
                """
                %' and h.year = 2018 and h.status = 'homeless'
                Group by l.lga_code 
                order by sum(count) desc;""";

                if (state_digit.equals("9")) {
                    query = """
                    select lga_name, sum(count)
                    from LGAStatistics as s 
                    join LGA as l on l.lga_code = s.lga_code 
                    join Homeless_atRisk as h on l.lga_code = h.lga_code
                    where h.year = 2018 and h.status = 'homeless'
                    Group by l.lga_code
                    order by sum(count) desc;""";
                }

                
                 // Get Result
                 ResultSet results = statement.executeQuery(query);
    
                // Process all of the results
                while (results.next()) {
                    // Lookup the columns we need
                    String resultsfinal = results.getString("lga_name");
                    HighestLga.add(resultsfinal);
                }
    
                 // Close the statement because we are done with it
                 statement.close();
             } catch (SQLException e) {
                 // If there is an error, lets just pring the error
                 System.err.println(e.getMessage());
             } finally {
                 // Safety code to cleanup
                 try {
                     if (connection != null) {
                         connection.close();
                     }
                 } catch (SQLException e) {
                     // connection close failed.
                     System.err.println(e.getMessage());
                 }
             }
    
            // Finally we return all of the lga
            return HighestLga;
        }
    
    
        public ArrayList<Integer> getPopulation2018() {
            // Create the ArrayList of LGA objects to return
            ArrayList<Integer> population2018 = new ArrayList<Integer>();
    
            // Setup the variable for the JDBC connection
            Connection connection = null;
    
            try {
                // Connect to JDBC data base
                connection = DriverManager.getConnection(DATABASE);
    
                // Prepare a new SQL Query & Set a timeout
                Statement statement = connection.createStatement();
                statement.setQueryTimeout(30);
    
                // The Query
                String query = "select sum(population) from LGAStatistics where year = 2018;";
                
                // Get Result
                ResultSet results = statement.executeQuery(query);
    
                // Process all of the results
                while (results.next()) {
                    // Lookup the columns we need
                    int results2018     = results.getInt("sum(population)");
                    population2018.add(results2018);
                }
    
                // Close the statement because we are done with it
                statement.close();
            } catch (SQLException e) {
                // If there is an error, lets just pring the error
                System.err.println(e.getMessage());
            } finally {
                // Safety code to cleanup
                try {
                    if (connection != null) {
                        connection.close();
                    }
                } catch (SQLException e) {
                    // connection close failed.
                    System.err.println(e.getMessage());
                }
            }
    
            // Finally we return all of the lga
            return population2018;
        }



        public ArrayList<Integer> medianValues(String state, String lga) {
            // Create the ArrayList of LGA objects to return
            ArrayList<Integer> median_values = new ArrayList<Integer>();
    
             // Setup the variable for the JDBC connection
             Connection connection = null;
    
             try {
                 // Connect to JDBC data base
                 connection = DriverManager.getConnection(DATABASE);
    
                // Prepare a new SQL Query & Set a timeout
                Statement statement = connection.createStatement();
                statement.setQueryTimeout(30);

                String state_digit = "0";

                if (state == null || state.equals("All")) {
                    state_digit = "9";
                }
                else {
                    if (state.equals("NSW")) {
                        state_digit = "1";
                    }
                    if (state.equals("VIC")) {
                        state_digit = "2";
                    }
                    if (state.equals("QLD")) {
                        state_digit = "3";
                    }
                    if (state.equals("SA")) {
                        state_digit = "4";
                    }
                    if (state.equals("WA")) {
                        state_digit = "5";
                    }
                    if (state.equals("TAS")) {
                        state_digit = "6";
                    }
                    if (state.equals("NT")) {
                        state_digit = "7";
                    }
                } 

                // The Query
                String query = ("select avg(median_age) from LGAStatistics where lga_code like '" + state_digit + "%' and year = 2018;");
                String query2 = ("select avg(median_mortgage_repay_monthly) from LGAStatistics where lga_code like '" + state_digit + "%' and year = 2018;");
                String query3 = ("select avg(median_rent_weekly) from LGAStatistics where lga_code like '" + state_digit + "%' and year = 2018;");
                String query4 = ("select avg(median_household_weekly_income) from LGAStatistics where lga_code like '" + state_digit + "%' and year = 2018;");

                if (lga == null || lga.equals("All")) {
                    if (state_digit.equals("9")) {
                        query = "select avg(median_age) from LGAStatistics where year = 2018;";
                        query2 = "select avg(median_mortgage_repay_monthly) from LGAStatistics where year = 2018;";
                        query3 = "select avg(median_rent_weekly) from LGAStatistics where year = 2018;";
                        query4 = "select avg(median_household_weekly_income) from LGAStatistics where year = 2018;";
                    }
                }
                else {
                    query = ("select avg(median_age) from LGAStatistics as s join LGA as l on l.lga_code = s.lga_code where lga_name = '" + lga + "' and year = 2018;");
                    query2 = ("select avg(median_mortgage_repay_monthly) from LGAStatistics as s join LGA as l on l.lga_code = s.lga_code where lga_name = '" + lga + "' and year = 2018;");
                    query3 = ("select avg(median_rent_weekly) from LGAStatistics as s join LGA as l on l.lga_code = s.lga_code where lga_name = '" + lga + "' and year = 2018;");
                    query4 = ("select avg(median_household_weekly_income) from LGAStatistics as s join LGA as l on l.lga_code = s.lga_code where lga_name = '" + lga + "' and year = 2018;");
                }
                
                 // Get Result
                 ResultSet results = statement.executeQuery(query);
    
                // Process all of the results
                while (results.next()) {
                    // Lookup the columns we need
                    int resultsfinal     = results.getInt("avg(median_age)");
                   // int resultsfinal2     = results2.getInt("avg(median_mortgage_repay_monthly)");
                    median_values.add(resultsfinal);
                  //  median_values.add(resultsfinal2);
                }

                ResultSet results2 = statement.executeQuery(query2);

                while (results2.next()) {
                    // Lookup the columns we need
                    int resultsfinal2     = results2.getInt("avg(median_mortgage_repay_monthly)");
                    median_values.add(resultsfinal2);
                }

                ResultSet results3 = statement.executeQuery(query3);

                while (results3.next()) {
                    // Lookup the columns we need
                    int resultsfinal3     = results3.getInt("avg(median_rent_weekly)");
                    median_values.add(resultsfinal3);
                }

                ResultSet results4 = statement.executeQuery(query4);

                while (results4.next()) {
                    // Lookup the columns we need
                    int resultsfinal4     = results4.getInt("avg(median_household_weekly_income)");
                    median_values.add(resultsfinal4);
                }
    
                 // Close the statement because we are done with it
                 statement.close();
             } catch (SQLException e) {
                 // If there is an error, lets just pring the error
                 System.err.println(e.getMessage());
             } finally {
                 // Safety code to cleanup
                 try {
                     if (connection != null) {
                         connection.close();
                     }
                 } catch (SQLException e) {
                     // connection close failed.
                     System.err.println(e.getMessage());
                 }
             }
    
            // Finally we return all of the lga
            return median_values;
        }

        public ArrayList<Integer> ST31_stats(String state, String lga, String sex, String range) {
            // Create the ArrayList of LGA objects to return
            ArrayList<Integer> stats_ST31 = new ArrayList<Integer>();
    
             // Setup the variable for the JDBC connection
             Connection connection = null;
    
             try {
                 // Connect to JDBC data base
                 connection = DriverManager.getConnection(DATABASE);
    
                // Prepare a new SQL Query & Set a timeout
                Statement statement = connection.createStatement();
                statement.setQueryTimeout(30);

                String state_digit = "0";

                if (state == null || state.equals("All")) {
                    state_digit = "9";
                }
                else {
                    if (state.equals("NSW")) {
                        state_digit = "1";
                    }
                    if (state.equals("VIC")) {
                        state_digit = "2";
                    }
                    if (state.equals("QLD")) {
                        state_digit = "3";
                    }
                    if (state.equals("SA")) {
                        state_digit = "4";
                    }
                    if (state.equals("WA")) {
                        state_digit = "5";
                    }
                    if (state.equals("TAS")) {
                        state_digit = "6";
                    }
                    if (state.equals("NT")) {
                        state_digit = "7";
                    }
                } 

                // The Query
                // get homeless population
                String query = """
                    select sum(population)
                    from LGAStatistics as s
                    join LGA as l on l.lga_code = s.lga_code
                    where l.lga_name = '""" + lga + "' and year = 2018;";
                
                String query2 = """
                    select sum(count) 
                    from LGA as l
                    join Homeless_atRisk as r on r.lga_code = l.lga_code
                    where l.lga_name = '""" + lga + "' and year = 2018 and status = 'homeless';";
                
                if (lga == null || lga.equals("All")) {
                    query = "select sum(population) from LGAStatistics where year = 2018"; 
                    query2 = """
                    select sum(count) 
                    from Homeless_atRisk
                    Where year = 2018 and status = 'homeless';
                    """;   
                }

                
                 // Get Result
                ResultSet results = statement.executeQuery(query);
    
                // Process all of the results
                while (results.next()) {
                    // Lookup the columns we need
                    int resultsfinal     = results.getInt("sum(population)");
                    stats_ST31.add(resultsfinal);
                }

                ResultSet results2 = statement.executeQuery(query2);
    
                // Process all of the results
                while (results2.next()) {
                    // Lookup the columns we need
                    int resultsfinal2     = results2.getInt("sum(count)");
                    stats_ST31.add(resultsfinal2);
                }
    
                 // Close the statement because we are done with it
                 statement.close();
             } catch (SQLException e) {
                 // If there is an error, lets just pring the error
                 System.err.println(e.getMessage());
             } finally {
                 // Safety code to cleanup
                 try {
                     if (connection != null) {
                         connection.close();
                     }
                 } catch (SQLException e) {
                     // connection close failed.
                     System.err.println(e.getMessage());
                 }
             }
    
            // Finally we return all of the lga
            return stats_ST31;
        }

        public ArrayList<Integer> filtered_LGA(String state, String median_income_min, String median_income_max, String median_mortgage_min,  String median_mortgage_max, String median_rent_min, String median_rent_max, String age_group, String sex ) {
            // Create the ArrayList of LGA objects to return
            ArrayList<Integer> filtered_lga = new ArrayList<Integer>();
    
             // Setup the variable for the JDBC connection
             Connection connection = null;
    
             try {
                 // Connect to JDBC data base
                 connection = DriverManager.getConnection(DATABASE);
    
                // Prepare a new SQL Query & Set a timeout
                Statement statement = connection.createStatement();
                statement.setQueryTimeout(30);

                String state_digit = "0";

                if (state == null || state.equals("All")) {
                    state_digit = "9";
                }
                else {
                    if (state.equals("NSW")) {
                        state_digit = "1";
                    }
                    if (state.equals("VIC")) {
                        state_digit = "2";
                    }
                    if (state.equals("QLD")) {
                        state_digit = "3";
                    }
                    if (state.equals("SA")) {
                        state_digit = "4";
                    }
                    if (state.equals("WA")) {
                        state_digit = "5";
                    }
                    if (state.equals("TAS")) {
                        state_digit = "6";
                    }
                    if (state.equals("NT")) {
                        state_digit = "7";
                    }
                } 

                // define max and min
                if (median_income_min == null || median_income_min.equals("min")) {
                    median_income_min = "0";
                }
                if (median_income_max == null || median_income_max.equals("max")) {
                    median_income_max = "4000";
                }
                if (median_mortgage_min == null || median_mortgage_min.equals("min")) {
                    median_mortgage_min = "0";
                }
                if (median_mortgage_max == null || median_mortgage_max.equals("max")) {
                    median_mortgage_max = "4000";
                }
                if (median_rent_min == null || median_rent_min.equals("min")) {
                    median_rent_min = "0";
                }
                if (median_rent_max == null || median_rent_max.equals("max")) {
                    median_rent_max = "4000";
                }

                // The Query
                String query = """
                SELECT sum(population), count()
                FROM LGA as l
                JOIN LGAStatistics s on l.lga_code = s.lga_code
                where median_household_weekly_income > """ 
                + median_income_min + " and median_household_weekly_income < " + median_income_max + " and median_mortgage_repay_monthly > " 
                + median_mortgage_min + " and median_mortgage_repay_monthly < " + median_mortgage_max + " and median_rent_weekly > " + median_rent_min 
                + " and median_rent_weekly < " + median_rent_max + " and l.lga_code like '" + state_digit + "%' and s.year = 2018;";

                String query2 = """
                    SELECT sum(count)
                    FROM LGA as l
                    JOIN LGAStatistics s on l.lga_code = s.lga_code
                    JOIN Homeless_atRisk as r on r.lga_code = l.lga_code
                    where median_household_weekly_income > """ + median_income_min + " and median_household_weekly_income < " + median_income_max 
                    + " and median_mortgage_repay_monthly > " + median_mortgage_min + " and median_mortgage_repay_monthly < " + median_mortgage_max 
                    + " and median_rent_weekly > " + median_rent_min + " and median_rent_weekly < " + median_rent_max + " and l.lga_code like '"
                    + state_digit + "%' and status = 'homeless' and r.year = 2018;";

                if (age_group.equals("All") & sex.equals("All")) {
                    query2 = """
                        SELECT sum(count)
                        FROM LGA as l
                        JOIN LGAStatistics s on l.lga_code = s.lga_code
                        JOIN Homeless_atRisk as r on r.lga_code = l.lga_code
                        where median_household_weekly_income > """ + median_income_min + " and median_household_weekly_income < "
                         + median_income_max + " and median_mortgage_repay_monthly > " + median_mortgage_min + " and median_mortgage_repay_monthly < " 
                         + median_mortgage_max + " and median_rent_weekly > " + median_rent_min + " and median_rent_weekly < " + median_rent_max 
                         + " and l.lga_code like '" + state_digit + "%' and status = 'homeless' and r.year = 2018;";
                }
                else if (age_group.equals("All") & !sex.equals("All")) {
                    query2 = """
                        SELECT sum(count)
                        FROM LGA as l
                        JOIN LGAStatistics s on l.lga_code = s.lga_code
                        JOIN Homeless_atRisk as r on r.lga_code = l.lga_code
                        where median_household_weekly_income > """ + median_income_min + " and median_household_weekly_income < " + median_income_max 
                        + " and median_mortgage_repay_monthly > " + median_mortgage_min + " and median_mortgage_repay_monthly < " + median_mortgage_max 
                        + " and median_rent_weekly > " + median_rent_min + " and median_rent_weekly < " + median_rent_max + " and l.lga_code like '" 
                        + state_digit + "%' and status = 'homeless' and sex = '" + sex + "' and r.year = 2018;";
                }
                else if (!age_group.equals("All") & sex.equals("All")) {
                    query2 = """
                        SELECT sum(count)
                        FROM LGA as l
                        JOIN LGAStatistics s on l.lga_code = s.lga_code
                        JOIN Homeless_atRisk as r on r.lga_code = l.lga_code
                        where median_household_weekly_income > """ + median_income_min + " and median_household_weekly_income < " + median_income_max 
                        + " and median_mortgage_repay_monthly > " + median_mortgage_min + " and median_mortgage_repay_monthly < " + median_mortgage_max 
                        + " and median_rent_weekly > " + median_rent_min + " and median_rent_weekly < " + median_rent_max + " and l.lga_code like '" 
                        + state_digit + "%' and status = 'homeless' and age_group = '" + age_group + "' and r.year = 2018;";
                }
                else {
                    query2 = """
                        SELECT sum(count)
                        FROM LGA as l
                        JOIN LGAStatistics s on l.lga_code = s.lga_code
                        JOIN Homeless_atRisk as r on r.lga_code = l.lga_code
                        where median_household_weekly_income > """ + median_income_min + " and median_household_weekly_income < " + median_income_max
                         + " and median_mortgage_repay_monthly > " + median_mortgage_min + " and median_mortgage_repay_monthly < " + median_mortgage_max
                          + " and median_rent_weekly > " + median_rent_min + " and median_rent_weekly < " + median_rent_max + " and l.lga_code like '" 
                          + state_digit + "%' and status = 'homeless' and age_group = '" + age_group + "' and sex = '" + sex + "' and r.year = 2018;";
                }
               
        
        

                if (state_digit.equals("9")) {
                    query = """
                        SELECT sum(population), count()
                        FROM LGA as l
                        JOIN LGAStatistics s on l.lga_code = s.lga_code
                        where median_household_weekly_income > """ + median_income_min + " and median_household_weekly_income < " + median_income_max + " and median_mortgage_repay_monthly > " + median_mortgage_min + " and median_mortgage_repay_monthly < " + median_mortgage_max + " and median_rent_weekly > " + median_rent_min + " and median_rent_weekly < " + median_rent_max + " and s.year = 2018;";
                     if (age_group.equals("All") & sex.equals("All")) {
                     query2 = """
                        SELECT sum(count)
                        FROM LGA as l
                        JOIN LGAStatistics s on l.lga_code = s.lga_code
                        JOIN Homeless_atRisk as r on r.lga_code = l.lga_code
                        where median_household_weekly_income > """ + median_income_min + " and median_household_weekly_income < " + median_income_max + " and median_mortgage_repay_monthly > " + median_mortgage_min + " and median_mortgage_repay_monthly < " + median_mortgage_max + " and median_rent_weekly > " + median_rent_min + " and median_rent_weekly < " + median_rent_max + " and status = 'homeless' and r.year = 2018;";
                    }
                    else if (age_group.equals("All") & !sex.equals("All")) {
                        query2 = """
                            SELECT sum(count)
                            FROM LGA as l
                            JOIN LGAStatistics s on l.lga_code = s.lga_code
                            JOIN Homeless_atRisk as r on r.lga_code = l.lga_code
                            where median_household_weekly_income > """ + median_income_min + " and median_household_weekly_income < " + median_income_max + " and median_mortgage_repay_monthly > " + median_mortgage_min + " and median_mortgage_repay_monthly < " + median_mortgage_max + " and median_rent_weekly > " + median_rent_min + " and median_rent_weekly < " + median_rent_max + " and status = 'homeless' and sex = '" + sex + "' and r.year = 2018;";
                    }
                    else if (!age_group.equals("All") & sex.equals("All")) {
                        query2 = """
                            SELECT sum(count)
                            FROM LGA as l
                            JOIN LGAStatistics s on l.lga_code = s.lga_code
                            JOIN Homeless_atRisk as r on r.lga_code = l.lga_code
                            where median_household_weekly_income > """ + median_income_min + " and median_household_weekly_income < " + median_income_max + " and median_mortgage_repay_monthly > " + median_mortgage_min + " and median_mortgage_repay_monthly < " + median_mortgage_max + " and median_rent_weekly > " + median_rent_min + " and median_rent_weekly < " + median_rent_max + " and status = 'homeless' and age_group = '" + age_group + "' and r.year = 2018;";
                    }
                    else {
                        query2 = """
                            SELECT sum(count)
                            FROM LGA as l
                            JOIN LGAStatistics s on l.lga_code = s.lga_code
                            JOIN Homeless_atRisk as r on r.lga_code = l.lga_code
                            where median_household_weekly_income > """ + median_income_min + " and median_household_weekly_income < " + median_income_max + " and median_mortgage_repay_monthly > " + median_mortgage_min + " and median_mortgage_repay_monthly < " + median_mortgage_max + " and median_rent_weekly > " + median_rent_min + " and median_rent_weekly < " + median_rent_max + " and status = 'homeless' and age_group = '" + age_group + "' and sex = '" + sex + "' and r.year = 2018;";
                    }
                }

                
                 // Get Result
                 ResultSet results = statement.executeQuery(query);
    
                // Process all of the results
                while (results.next()) {
                    // Lookup the columns we need
                    Integer resultsfinal = results.getInt("sum(population)");
                    Integer numberLGA = results.getInt("count()");
                    filtered_lga.add(resultsfinal);
                    filtered_lga.add(numberLGA);
                }

                ResultSet results2 = statement.executeQuery(query2);
    
                // Process all of the results
                while (results2.next()) {
                    // Lookup the columns we need
                    Integer resultsfinal2 = results.getInt("sum(count)");
                    filtered_lga.add(resultsfinal2);
                }
    
                 // Close the statement because we are done with it
                 statement.close();
             } catch (SQLException e) {
                 // If there is an error, lets just pring the error
                 System.err.println(e.getMessage());
             } finally {
                 // Safety code to cleanup
                 try {
                     if (connection != null) {
                         connection.close();
                     }
                 } catch (SQLException e) {
                     // connection close failed.
                     System.err.println(e.getMessage());
                 }
             }
    
            // Finally we return all of the lga
            return filtered_lga;
        }

        public ArrayList<String> ranked_LGA(String state, String median_income_min, String median_income_max, String median_mortgage_min,  String median_mortgage_max, String median_rent_min, String median_rent_max, String age_group, String sex, String median_sort, String order ) {
            // Create the ArrayList of LGA objects to return
            ArrayList<String> rank_lga = new ArrayList<String>();
    
             // Setup the variable for the JDBC connection
             Connection connection = null;
    
             try {
                 // Connect to JDBC data base
                 connection = DriverManager.getConnection(DATABASE);
    
                // Prepare a new SQL Query & Set a timeout
                Statement statement = connection.createStatement();
                statement.setQueryTimeout(30);

                String state_digit = "0";

                if (state == null || state.equals("All")) {
                    state_digit = "9";
                }
                else {
                    if (state.equals("NSW")) {
                        state_digit = "1";
                    }
                    if (state.equals("VIC")) {
                        state_digit = "2";
                    }
                    if (state.equals("QLD")) {
                        state_digit = "3";
                    }
                    if (state.equals("SA")) {
                        state_digit = "4";
                    }
                    if (state.equals("WA")) {
                        state_digit = "5";
                    }
                    if (state.equals("TAS")) {
                        state_digit = "6";
                    }
                    if (state.equals("NT")) {
                        state_digit = "7";
                    }
                } 

                // define max and min
                if (median_income_min == null || median_income_min.equals("min")) {
                    median_income_min = "0";
                }
                if (median_income_max == null || median_income_max.equals("max")) {
                    median_income_max = "4000";
                }
                if (median_mortgage_min == null || median_mortgage_min.equals("min")) {
                    median_mortgage_min = "0";
                }
                if (median_mortgage_max == null || median_mortgage_max.equals("max")) {
                    median_mortgage_max = "4000";
                }
                if (median_rent_min == null || median_rent_min.equals("min")) {
                    median_rent_min = "0";
                }
                if (median_rent_max == null || median_rent_max.equals("max")) {
                    median_rent_max = "4000";
                }

                // The Query

                String query = """
                    SELECT lga_name
                    FROM LGA as l
                    JOIN LGAStatistics s on l.lga_code = s.lga_code
                    JOIN Homeless_atRisk as r on r.lga_code = l.lga_code
                    where median_household_weekly_income > """ + median_income_min + " and median_household_weekly_income < " + median_income_max + " and median_mortgage_repay_monthly > " + median_mortgage_min + " and median_mortgage_repay_monthly < " + median_mortgage_max + " and median_rent_weekly > " + median_rent_min + " and median_rent_weekly < " + median_rent_max + " and l.lga_code like '" + state_digit + "%' and status = 'homeless' and r.year = 2018 order by " + median_sort + " " + order + ";";

                if (age_group.equals("All") & sex.equals("All")) {
                    query = """
                        SELECT lga_name
                        FROM LGA as l
                        JOIN LGAStatistics s on l.lga_code = s.lga_code
                        JOIN Homeless_atRisk as r on r.lga_code = l.lga_code
                        where median_household_weekly_income > """ + median_income_min + " and median_household_weekly_income < " + median_income_max + " and median_mortgage_repay_monthly > " + median_mortgage_min + " and median_mortgage_repay_monthly < " + median_mortgage_max + " and median_rent_weekly > " + median_rent_min + " and median_rent_weekly < " + median_rent_max + " and l.lga_code like '" + state_digit + "%' and status = 'homeless' and r.year = 2018 order by " + median_sort + " " + order + ";";
                }
                else if (age_group.equals("All") & !sex.equals("All")) {
                    query = """
                        SELECT lga_name
                        FROM LGA as l
                        JOIN LGAStatistics s on l.lga_code = s.lga_code
                        JOIN Homeless_atRisk as r on r.lga_code = l.lga_code
                        where median_household_weekly_income > """ + median_income_min + " and median_household_weekly_income < " + median_income_max + " and median_mortgage_repay_monthly > " + median_mortgage_min + " and median_mortgage_repay_monthly < " + median_mortgage_max + " and median_rent_weekly > " + median_rent_min + " and median_rent_weekly < " + median_rent_max + " and l.lga_code like '" + state_digit + "%' and status = 'homeless' and sex = '" + sex + "' and r.year = 2018 order by " + median_sort + " " + order + ";";
                }
                else if (!age_group.equals("All") & sex.equals("All")) {
                    query = """
                        SELECT lga_name
                        FROM LGA as l
                        JOIN LGAStatistics s on l.lga_code = s.lga_code
                        JOIN Homeless_atRisk as r on r.lga_code = l.lga_code
                        where median_household_weekly_income > """ + median_income_min + " and median_household_weekly_income < " + median_income_max + " and median_mortgage_repay_monthly > " + median_mortgage_min + " and median_mortgage_repay_monthly < " + median_mortgage_max + " and median_rent_weekly > " + median_rent_min + " and median_rent_weekly < " + median_rent_max + " and l.lga_code like '" + state_digit + "%' and status = 'homeless' and age_group = '" + age_group + "' and r.year = 2018 order by " + median_sort + " " + order + ";";
                }
                else {
                    query = """
                        SELECT lga_name
                        FROM LGA as l
                        JOIN LGAStatistics s on l.lga_code = s.lga_code
                        JOIN Homeless_atRisk as r on r.lga_code = l.lga_code
                        where median_household_weekly_income > """ + median_income_min + " and median_household_weekly_income < " + median_income_max + " and median_mortgage_repay_monthly > " + median_mortgage_min + " and median_mortgage_repay_monthly < " + median_mortgage_max + " and median_rent_weekly > " + median_rent_min + " and median_rent_weekly < " + median_rent_max + " and l.lga_code like '" + state_digit + "%' and status = 'homeless' and age_group = '" + age_group + "' and sex = '" + sex + "' and r.year = 2018 order by " + median_sort + " " + order + ";";
                }
               
        
        

                if (state_digit.equals("9")) {
                     if (age_group.equals("All") & sex.equals("All")) {
                     query = """
                        SELECT lga_name
                        FROM LGA as l
                        JOIN LGAStatistics s on l.lga_code = s.lga_code
                        JOIN Homeless_atRisk as r on r.lga_code = l.lga_code
                        where median_household_weekly_income > """ + median_income_min + " and median_household_weekly_income < " + median_income_max + " and median_mortgage_repay_monthly > " + median_mortgage_min + " and median_mortgage_repay_monthly < " + median_mortgage_max + " and median_rent_weekly > " + median_rent_min + " and median_rent_weekly < " + median_rent_max + " and status = 'homeless' and r.year = 2018 order by " + median_sort + " " + order + ";";
                    }
                    else if (age_group.equals("All") & !sex.equals("All")) {
                        query = """
                            SELECT lga_name
                            FROM LGA as l
                            JOIN LGAStatistics s on l.lga_code = s.lga_code
                            JOIN Homeless_atRisk as r on r.lga_code = l.lga_code
                            where median_household_weekly_income > """ + median_income_min + " and median_household_weekly_income < " + median_income_max + " and median_mortgage_repay_monthly > " + median_mortgage_min + " and median_mortgage_repay_monthly < " + median_mortgage_max + " and median_rent_weekly > " + median_rent_min + " and median_rent_weekly < " + median_rent_max + " and status = 'homeless' and sex = '" + sex + "' and r.year = 2018 order by " + median_sort + " " + order + ";";
                    }
                    else if (!age_group.equals("All") & sex.equals("All")) {
                        query = """
                            SELECT lga_name
                            FROM LGA as l
                            JOIN LGAStatistics s on l.lga_code = s.lga_code
                            JOIN Homeless_atRisk as r on r.lga_code = l.lga_code
                            where median_household_weekly_income > """ + median_income_min + " and median_household_weekly_income < " + median_income_max + " and median_mortgage_repay_monthly > " + median_mortgage_min + " and median_mortgage_repay_monthly < " + median_mortgage_max + " and median_rent_weekly > " + median_rent_min + " and median_rent_weekly < " + median_rent_max + " and status = 'homeless' and age_group = '" + age_group + "' and r.year = 2018 order by " + median_sort + " " + order + ";";
                    }
                    else {
                        query = """
                            SELECT lga_name
                            FROM LGA as l
                            JOIN LGAStatistics s on l.lga_code = s.lga_code
                            JOIN Homeless_atRisk as r on r.lga_code = l.lga_code
                            where median_household_weekly_income > """ + median_income_min + " and median_household_weekly_income < " + median_income_max + " and median_mortgage_repay_monthly > " + median_mortgage_min + " and median_mortgage_repay_monthly < " + median_mortgage_max + " and median_rent_weekly > " + median_rent_min + " and median_rent_weekly < " + median_rent_max + " and status = 'homeless' and age_group = '" + age_group + "' and sex = '" + sex + "' and r.year = 2018 order by " + median_sort + " " + order + ";";
                    }
                }

                
                 // Get Result
                 ResultSet results = statement.executeQuery(query);
    
                // Process all of the results
                while (results.next()) {
                    // Lookup the columns we need
                    String resultsfinal = results.getString("lga_name");
                    rank_lga.add(resultsfinal);
                }
    
                 // Close the statement because we are done with it
                 statement.close();
             } catch (SQLException e) {
                 // If there is an error, lets just pring the error
                 System.err.println(e.getMessage());
             } finally {
                 // Safety code to cleanup
                 try {
                     if (connection != null) {
                         connection.close();
                     }
                 } catch (SQLException e) {
                     // connection close failed.
                     System.err.println(e.getMessage());
                 }
             }
    
            // Finally we return all of the lga
            return rank_lga;
        }


   
    }

    
