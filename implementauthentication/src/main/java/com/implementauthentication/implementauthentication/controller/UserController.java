package com.implementauthentication.implementauthentication.controller;

import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import com.implementauthentication.implementauthentication.model.User;

import com.implementauthentication.implementauthentication.repository.UserRepository;

import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/User")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    public UserController()
    {

    }

    public UserController(UserRepository userRepository)
    {
        this.userRepository = userRepository;
    }


    @GetMapping("")
    public ModelAndView getlogin()
    {
        ModelAndView mav = new ModelAndView("login.html");
        User newUser = new User();
        mav.addObject("user", newUser);
        return mav;
    }

    @PostMapping("login")
    public RedirectView postlogin(@RequestParam("email") String email, @RequestParam("password") String password, HttpSession session)
    {
        User dbUser = (User) this.userRepository.findByEmail(email);
        if(dbUser != null)
        {
            Boolean isRightPassword = BCrypt.checkpw(password, dbUser.getPassword());
            if(isRightPassword)
            {
                session.setAttribute("user", dbUser);
                return new RedirectView("/User/profile");
            }
            else
            {
                return new RedirectView("/User?error=wrongPassword");
            }
        }
        else
        {
            return new RedirectView("/User?error=userNotFound");
        }
    }

    @GetMapping("/signup")
    public ModelAndView getSignUp()
    {
        ModelAndView mav = new ModelAndView("signup.html");
        User newUser = new User();
        mav.addObject("user", newUser);
        return mav;
    }

    @PostMapping("/signup")
    public RedirectView postSignUp(@ModelAttribute User user)
    {
        User dbUser = (User) this.userRepository.findByEmail(user.getEmail());
        if(dbUser == null)
        {
             String encodePassword = BCrypt.hashpw(user.getPassword(), BCrypt.gensalt(12));
             user.setPassword(encodePassword);
             this.userRepository.save(user);
             return new RedirectView("/User");
        }
        else
        {
            return new RedirectView("/User/signup?error=userAlreadySignedUp");
        }
    
    }

    @GetMapping("/profile")
    public ModelAndView getProfile(HttpSession session)
    {
        ModelAndView mav = new ModelAndView("profile.html");
        User sessionUser = (User) session.getAttribute("user");
        mav.addObject("user", sessionUser);
        return mav;
    }

    @GetMapping("/logout")
    public ModelAndView logout(HttpSession session)
    {
        ModelAndView mav = new ModelAndView();
        session.invalidate();
        mav.setViewName("redirect:/User");
        return mav;
    }

}
