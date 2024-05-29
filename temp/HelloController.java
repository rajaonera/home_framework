package mg.tests.controller;

import mg.framework.annotations.Controller;
import mg.framework.annotations.Get;

@Controller
public class HelloController {
    @Get("/index")
    public void index(){
        System.out.println("HelloController");
    }

    @Get("/hello")
    public void hello (){
        System.out.println("hello word !");
    }
}
