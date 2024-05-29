package mg.tests.controller;

import mg.framework.annotations.Controller;
import mg.framework.annotations.Get;

@Controller
public class TestController {
    @Get("/index")
    public void index(){
        System.out.println("TestController");
    }
    @Get("/Test")
    public void test(){
        System.out.println("Test the word !");
    }
}
