package mg.tests.controller;

import mg.framework.annotations.Controller;

@Controller
public class HelloController {
    public void index(){
        System.out.println("HelloController");
    }
}
