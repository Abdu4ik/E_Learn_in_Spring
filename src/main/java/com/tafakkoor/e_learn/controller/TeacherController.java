package com.tafakkoor.e_learn.controller;

import com.tafakkoor.e_learn.config.security.UserSession;
import com.tafakkoor.e_learn.domain.AuthUser;
import com.tafakkoor.e_learn.domain.Content;
import com.tafakkoor.e_learn.dto.ContentUpdateDTO;
import com.tafakkoor.e_learn.dto.GrammarCreateDTO;
import com.tafakkoor.e_learn.dto.StoryCreateDTO;
import com.tafakkoor.e_learn.enums.ContentType;
import com.tafakkoor.e_learn.enums.Levels;
import com.tafakkoor.e_learn.services.ContentService;
import com.tafakkoor.e_learn.services.TeacherService;
import com.tafakkoor.e_learn.services.UserService;
import com.tafakkoor.e_learn.utils.helpers.QuestionHelper;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

@Controller
@RequestMapping("/teacher")
@PreAuthorize("hasRole('TEACHER')")
public class TeacherController {
    private final TeacherService teacherService;
    private final ContentService contentService;
    private final UserSession userSession;
    private final UserService userService;


    public TeacherController(TeacherService teacherService, ContentService contentService, UserSession userSession, UserService userService) {
        this.teacherService = teacherService;
        this.contentService = contentService;
        this.userSession = userSession;
        this.userService = userService;
    }

    @GetMapping("")
    String teacherPage() {
        return "teacher/home";
    }


    @GetMapping("/reading")
    public ModelAndView reading() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("teacher/story/main");
        List<Content> allStories = teacherService.getAllStories();
        modelAndView.addObject("stories", allStories);
        return modelAndView;
    }


    @GetMapping("/reading/create")
    public ModelAndView readingCreate() {
        ModelAndView mo = new ModelAndView();
        List<Levels> levels = userService.getAllLevels();
        mo.addObject("levels", levels);
        mo.addObject("dto", new StoryCreateDTO());
        mo.setViewName("teacher/story/create");
        return mo;
    }

    @PostMapping("/reading/create")
    public String readingCreatePage(@ModelAttribute("dto") StoryCreateDTO dto, Model model, BindingResult errors) {
        if (errors.hasErrors()) {
            List<Levels> allLevels = userService.getAllLevels();
            model.addAttribute("levels", allLevels);
            return "teacher/story/create";
        }
        dto.setContentType(ContentType.STORY);
        contentService.storyCreate(dto);
        return "redirect:/teacher/reading";
    }

    @GetMapping("/reading/update/{id}")
    public String updateStoryPage(Model model, @PathVariable Long id) {
        Content story = teacherService.getContentByIdContentType(id, ContentType.STORY);
        List<Levels> levels = userService.getAllLevels();
        model.addAttribute("levels", levels);
        model.addAttribute("story", story);
        return "teacher/story/update";
    }

    @PostMapping("/reading/update/{id}")
    public String updateStory(@ModelAttribute("dto") ContentUpdateDTO dto, BindingResult errors, @PathVariable Long id) {
        if (errors.hasErrors())
            return "teacher/story/update";
        dto.setContentType(ContentType.STORY);
        Content content = contentService.findById(id);
        contentService.update(dto, id, content.getDocument().getId());
        return "redirect:/teacher/reading";
    }


    @GetMapping("/reading/delete/{id}")
    public String deleteStory(Model model, @PathVariable Long id) {
        Content storyById = teacherService.getContentByIdContentType(id, ContentType.STORY);
        model.addAttribute("story", storyById);  // todo htmlda content ni nullga tekshirish kerak
        return "teacher/story/delete";
    }

    @PostMapping("/reading/delete/{id}")
    public String deleteStoryPage(@PathVariable Long id) {
        teacherService.deleteStoryById(id, userSession.getUser().getId());
        return "redirect:/teacher/reading";
    }


    @GetMapping("/grammar")
    public ModelAndView grammar() {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("teacher/grammar/main");
        List<Content> allGrammars = teacherService.getAllGrammars();
        modelAndView.addObject("grammars", allGrammars);
        return modelAndView;
    }


    @GetMapping("/grammar/create")
    public ModelAndView grammarCreate() {
        ModelAndView mo = new ModelAndView();
        List<Levels> levels = userService.getAllLevels();
        mo.addObject("levels", levels);
        mo.setViewName("teacher/grammar/create");
        return mo;
    }

    @PostMapping("/grammar/create")
    public String grammarCreatePage(@ModelAttribute("dto") GrammarCreateDTO dto, BindingResult errors) {
        if (errors.hasErrors())
            return "teacher/grammar/create";
        dto.setContentType(ContentType.GRAMMAR);
        contentService.grammarCreate(dto);
        return "redirect:/teacher/grammar";
    }

    @GetMapping("/grammar/update/{id}")
    public String updateGrammarPage(Model model, @PathVariable Long id) {
        Content grammar = teacherService.getContentByIdContentType(id, ContentType.GRAMMAR);
        List<Levels> levels = userService.getAllLevels();
        model.addAttribute("levels", levels);
        model.addAttribute("grammar", grammar);
        return "teacher/grammar/update";
    }

    @PostMapping("/grammar/update/{id}")
    public String updateGrammar(@ModelAttribute("dto") ContentUpdateDTO dto, BindingResult errors, @PathVariable Long id) {
        if (errors.hasErrors())
            return "teacher/grammar/update";
        dto.setContentType(ContentType.GRAMMAR);
        Content content = contentService.findById(id);
        contentService.update(dto, id, content.getDocument().getId());
        return "redirect:/teacher/grammar";
    }

    @GetMapping("/grammar/delete/{id}")
    public String deleteGrammar(Model model, @PathVariable Long id) {
        Content grammarById = teacherService.getContentByIdContentType(id, ContentType.GRAMMAR);
        model.addAttribute("grammar", grammarById);  // todo htmlda content ni nullga tekshirish kerak
        return "teacher/grammar/delete";

    }


    @PostMapping("/grammar/delete/{id}")
    public String deleteGrammarPage(@PathVariable Long id) {
        AuthUser authUser = userSession.getUser();
        teacherService.deleteGrammarById(id, authUser.getId());
        return "redirect:/teacher/grammar";
    }

    @GetMapping("/grammar/question/{id}")
    public ModelAndView questionList(@PathVariable Long id) {
        ModelAndView mo = new ModelAndView();
        mo.addObject("grammarId", id);
        mo.setViewName("teacher/grammar/question/main");
        mo.addObject("questions", contentService.findAllQuestionByGrammar(id));
        return mo;
    }


    @GetMapping("/grammar/question/create/{id}")
    public ModelAndView createQuestion(@PathVariable Long id) {
        ModelAndView mo = new ModelAndView();
        mo.addObject("grammarId", id);
        mo.setViewName("teacher/grammar/question/create");
        return mo;

    }


    @PostMapping("/grammar/question/create/{id}")
    public String createQuestionPage(@ModelAttribute("helper") QuestionHelper helper,@PathVariable Long id, BindingResult errors) {

        if (errors.hasErrors()) {
            return "redirect:/teacher/grammar/question/create/"+id;
        }
        contentService.saveQuestion(helper, id);
        return "redirect:/teacher/grammar/question/"+id;
    }

    @GetMapping("/grammar/question/delete/{questionId}")
    public ModelAndView deleteQuestionPage(@PathVariable Long questionId) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.addObject("questionId", questionId);
        Long grammarId = teacherService.getGrammarIdByQuestionId(questionId);
        modelAndView.addObject("grammarId", grammarId);
        modelAndView.setViewName("teacher/grammar/question/delete");
        return modelAndView;
    }


    @PostMapping("/grammar/question/delete/{questionId}")
    public String deleteQuestion(@ModelAttribute("grammarId") Long grammarId,@PathVariable Long questionId) {
        contentService.deleteQuestion(questionId);
        return "redirect:/teacher/grammar/question/"+grammarId;
    }


}
