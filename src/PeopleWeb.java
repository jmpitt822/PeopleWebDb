import spark.ModelAndView;
import spark.Session;
import spark.Spark;
import spark.template.mustache.MustacheTemplateEngine;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

public class PeopleWeb {
    static ArrayList<Person> people = new ArrayList<>();
    static Integer offset = 0;

    public static void main(String[] args) throws Exception {
        File f = new File("people.csv");
        Scanner fileScanner = new Scanner(f);

        String line;
        line = fileScanner.nextLine();

        while (fileScanner.hasNext()) {
            int counter = 0;
            while (counter < 20) {
                line = fileScanner.nextLine();
                String[] columns = line.split(",");
                Person person = new Person(Integer.parseInt(columns[0]), columns[1], columns[2], columns[3], columns[4], columns[5]);
                people.add(person);
                counter++;
            }
        }
        fileScanner.close();


        Spark.init();
        Spark.get("/",
                ((request, response) -> {
                    String offsetString = "0";
                    String previous = "Previous";
                    String next = "Next";
                    HashMap m = new HashMap();

                    Session session = request.session();
                    String firstName = session.attribute("???");

                    String twenty1 = request.queryParams("offset");

                    ArrayList twenty = new ArrayList();
                    if (twenty1 == null){
                        twenty1 = "0";
                    }
                    offset = offset+Integer.parseInt(twenty1);
//
                    for (int i = 0; i<20; i++){
                        Person person = people.get(offset+i);
                        twenty.add(person);
                    }
                    if(offset >= 20){
                        m.put("next", next);
                    }
                    if(offset <= 980){
                        m.put("next", next);
                    }
                    m.put("people", twenty);

                    return new ModelAndView(m, "people.html");
                }),
                new MustacheTemplateEngine()
        );

        Spark.get("/person",
                ((request, response) -> {
                    HashMap m = new HashMap();
                    int id = Integer.parseInt(request.queryParams("id"));
                    Person person = people.get(id-1);
                    m.put("person", person);

                    return new ModelAndView(m, "person.html");
                }),
                new MustacheTemplateEngine()
        );

    }
}
