package com.risky.gambling.controllers;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;

import com.risky.gambling.daos.TodoDAO;
import com.risky.gambling.mappers.TodoMapper;
import com.risky.gambling.models.Todo;
import com.risky.gambling.utils.ComparisonOperator;
import com.risky.gambling.utils.DataMapping;
import com.risky.gambling.utils.Helpers;
import com.risky.gambling.utils.ViewName;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class TodoController {
    
    @Autowired
    private TodoDAO todoDAO;

    @ModelAttribute("statusList")
    public Map<Integer, String> getStatusList() {
        Map<Integer, String> statusList = new HashMap<Integer, String>();
        statusList.put(Helpers.INCOMPLETE, "Incomplete");
        statusList.put(Helpers.COMPLETED, "Completed");
        return statusList;
    }

    static String currentPage = "1";
    static String recordPerPage = "3";
    
    @GetMapping("/todos")
    public String list(Model model) 
    {
        try {
            String orderBy = "created_at";
            String[] limit = {String.valueOf(Integer.parseInt(recordPerPage)*Integer.parseInt(currentPage) - Integer.parseInt(recordPerPage)), 
                              String.valueOf(Integer.parseInt(recordPerPage))};
            List<Todo> listPerPage = todoDAO.get(null, null, null, null, orderBy, limit);
            List<Todo> allItems = todoDAO.get(null, null, null, null, orderBy, null);
            Double totalPages = Math.ceil(allItems.size()/Integer.parseInt(recordPerPage));
            model.addAttribute("todos", listPerPage);
            model.addAttribute("allItems", allItems.size());
            model.addAttribute("totalPages", totalPages);
            return ViewName.TODO_LIST;
        } catch (Exception e) {
            return ViewName.ERROR_404;
        }
    }

    @GetMapping("/list")
    @ResponseBody
    public List<Todo> listPagination(@RequestParam(name = "currentPage", required = false, defaultValue = "1") String currentPage, 
                                    @RequestParam(name = "recordPerPage", required = false, defaultValue = "3") String recordPerPage) 
    {
        try {
            String orderBy = "created_at";
            String[] limit = {String.valueOf(Integer.parseInt(recordPerPage)*Integer.parseInt(currentPage) - Integer.parseInt(recordPerPage)), 
                              String.valueOf(Integer.parseInt(recordPerPage))};
            List<Todo> listPerPage = todoDAO.get(null, null, null, null, orderBy, limit);
            return listPerPage;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    @GetMapping("/todos/create")
    public String createView(Model model)
    {
        try {
            model.addAttribute("todo", new TodoMapper());
            return ViewName.TODO_DETAIL;
        } catch (Exception e) {
            return ViewName.ERROR_404;
        }
    }

    @PostMapping("/todos/create")
    public String createAction(@Valid @ModelAttribute("todo") TodoMapper todoMapper, BindingResult bindingResult, 
                                RedirectAttributes atts, Model model) {
        try {
            if(bindingResult.hasErrors()){
                model.addAttribute("oldData", todoMapper);
                return ViewName.TODO_DETAIL;
            }
            ArrayList<DataMapping> data = new ArrayList<DataMapping>();
            data.add(DataMapping.getInstance("title", todoMapper.getTitle()));
            data.add(DataMapping.getInstance("description", todoMapper.getDescription()));
            data.add(DataMapping.getInstance("start_date", todoMapper.getStartDate().toString()));
            data.add(DataMapping.getInstance("end_date", todoMapper.getEndDate().toString()));
            data.add(DataMapping.getInstance("created_at", LocalDateTime.now().toString()));
            data.add(DataMapping.getInstance("status", String.valueOf(todoMapper.getStatus())));
            Number created = todoDAO.create(data);
            if(created.intValue() > 0){
                atts.addFlashAttribute("success", "Successfully created new todo");
            } else {
                atts.addFlashAttribute("fail", "Whoops! Something went wrong, cannot create new todo");
            }
        } catch (Exception e) {
            atts.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/todos";
    }

    @GetMapping("/todos/update")
    public String updateView(@RequestParam int id, Model model)
    {
        try {
            ArrayList<ComparisonOperator> conditions = new ArrayList<ComparisonOperator>();
            conditions.add(ComparisonOperator.getInstance("", "id", "=", String.valueOf(id), ""));
            TodoMapper todoMapper = todoDAO.getById(conditions);
            model.addAttribute("todo", todoMapper);
            return ViewName.TODO_DETAIL;
        } catch (Exception e) {
            return ViewName.ERROR_404;
        }
    }

    @PostMapping("/todos/update")
    public String updateAction(@RequestParam int id, @Valid @ModelAttribute("todo") TodoMapper todoMapper, BindingResult bindingResult, 
                                  RedirectAttributes atts, Model model) {
        try {
            if(bindingResult.hasErrors()){
                model.addAttribute("oldData", todoMapper);
                return ViewName.TODO_DETAIL;
            }
            ArrayList<ComparisonOperator> conditions = new ArrayList<ComparisonOperator>();
            conditions.add(ComparisonOperator.getInstance("", "id", "=", String.valueOf(id), ""));
            TodoMapper dbItem = todoDAO.getById(conditions);
            ArrayList<DataMapping> data = new ArrayList<DataMapping>();
            if(!dbItem.getTitle().equals(todoMapper.getTitle())){
                data.add(DataMapping.getInstance("title", todoMapper.getTitle()));
            }
            if(!dbItem.getDescription().equals(todoMapper.getDescription())){
                data.add(DataMapping.getInstance("description", todoMapper.getDescription()));
            }
            if(!dbItem.getStartDate().equals(todoMapper.getStartDate())){
                data.add(DataMapping.getInstance("start_date", todoMapper.getStartDate().toString()));
            }
            if(!dbItem.getEndDate().equals(todoMapper.getEndDate())){
                data.add(DataMapping.getInstance("end_date", todoMapper.getEndDate().toString()));
            }
            if(!String.valueOf(dbItem.getStatus()).equals(String.valueOf(todoMapper.getStatus()))){
                data.add(DataMapping.getInstance("status", String.valueOf(todoMapper.getStatus())));
            }
            boolean updated = todoDAO.update(data, conditions);
            if(updated){
                atts.addFlashAttribute("success", "Successfully updated new todo");
            } else {
                atts.addFlashAttribute("fail", "Whoops! Something went wrong, cannot update new todo");
            }
        } catch (Exception e) {
            atts.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/todos";
    }
    
    @PostMapping("/todos/delete")
    public String deleteAction(@RequestParam int id, RedirectAttributes atts)
    {
        try {
            ArrayList<ComparisonOperator> conditions = new ArrayList<ComparisonOperator>();
            conditions.add(ComparisonOperator.getInstance("", "id", "=", String.valueOf(id), ""));
            boolean deleted = todoDAO.delete(conditions);
            if(deleted){
                atts.addFlashAttribute("success", "Successfully deleted new todo");
            } else {
                atts.addFlashAttribute("fail", "Whoops! Something went wrong, cannot delete new todo");
            }
        } catch (Exception e) {
            atts.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/todos";
    }
    
    @GetMapping("/search")
    @ResponseBody
    public List<Todo>  onSearchAll(@RequestParam(name = "search", required = false) String search, 
                               @RequestParam(name = "startDate", required = false) String startDate,
                               @RequestParam(name = "endDate", required = false) String endDate, 
                               @RequestParam(name = "status", required = false) String status, Model model)
    {
        try {
            ArrayList<ComparisonOperator> conditions = conditionList(search, startDate, endDate, status);
            String orderBy = "created_at";
            System.out.println("search:"+search+"-startDate:"+startDate);
            System.out.println("endDate:"+endDate+"-status:"+status);
            List<Todo> searchedItems = todoDAO.get(null, conditions, null, null, orderBy, null);
            model.addAttribute("searchedItems", searchedItems);
            return searchedItems;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    @GetMapping("/searchPagination")
    @ResponseBody
    public List<Todo> onSearchPagination(@RequestParam(name = "search", required = false) String search, 
                               @RequestParam(name = "startDate", required = false) String startDate,
                               @RequestParam(name = "endDate", required = false) String endDate, 
                               @RequestParam(name = "status", required = false) String status,
                               @RequestParam(name = "currentPage", required = false, defaultValue = "1") String currentPage, 
                               @RequestParam(name = "recordPerPage", required = false, defaultValue = "3") String recordPerPage) 
    {
        try {
            ArrayList<ComparisonOperator> conditions = conditionList(search, startDate, endDate, status);
            String orderBy = "created_at";
            System.out.println("search:"+search+"-startDate:"+startDate);
            System.out.println("endDate:"+endDate+"-status:"+status);
            String[] limit = {String.valueOf(Integer.parseInt(recordPerPage)*Integer.parseInt(currentPage) - Integer.parseInt(recordPerPage)), 
                              String.valueOf(Integer.parseInt(recordPerPage))};
            List<Todo> listPerPage = todoDAO.get(null, conditions, null, null, orderBy, limit);
            return listPerPage;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    public ArrayList<ComparisonOperator> conditionList(String search, String startDate, String endDate, String status)
    {
        ArrayList<ComparisonOperator> conditions = new ArrayList<ComparisonOperator>();
        //four
        if(search != null && startDate != null && endDate != null && status != null){
            conditions.add(ComparisonOperator.getInstance("(", "title", "like ", "%"+search+"%", ""));
            conditions.add(ComparisonOperator.getInstance(" or ", "description", "like ", "%"+search+"%", ")"));
            conditions.add(ComparisonOperator.getInstance(" and ", "start_date", ">=", startDate, ""));
            conditions.add(ComparisonOperator.getInstance(" and ", "end_date", "<=", endDate, ""));
            conditions.add(ComparisonOperator.getInstance(" and ", "status", "=", status, ""));
        }
        //one
        if(search != null && startDate == null && endDate == null && status == null){
            conditions.add(ComparisonOperator.getInstance("(", "title", "like ", "%"+search+"%", ""));
            conditions.add(ComparisonOperator.getInstance(" or ", "description", "like ", "%"+search+"%", ")"));
        }
        if(search == null && startDate != null && endDate == null && status == null){
            conditions.add(ComparisonOperator.getInstance("", "start_date", "=", startDate, ""));
        }
        if(search == null && startDate == null && endDate != null && status == null){
            conditions.add(ComparisonOperator.getInstance("", "end_date", "=", endDate, ""));
        }
        if(search == null && startDate == null && endDate == null && status != null){
            conditions.add(ComparisonOperator.getInstance("", "status", "=", status, ""));
        }
        //two
        if(search != null && startDate != null && endDate == null && status == null){
            conditions.add(ComparisonOperator.getInstance("(", "title", "like ", "%"+search+"%", ""));
            conditions.add(ComparisonOperator.getInstance(" or ", "description", "like ", "%"+search+"%", ")"));
            conditions.add(ComparisonOperator.getInstance(" and ", "start_date", "=", startDate, ""));
        }
        if(search == null && startDate != null && endDate != null && status == null){
            conditions.add(ComparisonOperator.getInstance("", "start_date", ">=", startDate, ""));
            conditions.add(ComparisonOperator.getInstance(" and ", "end_date", "<=", endDate, ""));
        }
        if(search == null && startDate == null && endDate != null && status != null){
            conditions.add(ComparisonOperator.getInstance("", "end_date", "=", endDate, ""));
            conditions.add(ComparisonOperator.getInstance(" and ", "status", "=", status, ""));
        }
        if(search != null && startDate == null && endDate != null && status == null){
            conditions.add(ComparisonOperator.getInstance("(", "title", "like ", "%"+search+"%", ""));
            conditions.add(ComparisonOperator.getInstance(" or ", "description", "like ", "%"+search+"%", ")"));
            conditions.add(ComparisonOperator.getInstance(" and ", "end_date", "=", endDate, ""));
        }
        if(search != null && startDate == null && endDate == null && status != null){
            conditions.add(ComparisonOperator.getInstance("(", "title", "like ", "%"+search+"%", ""));
            conditions.add(ComparisonOperator.getInstance(" or ", "description", "like ", "%"+search+"%", ")"));
            conditions.add(ComparisonOperator.getInstance(" and ", "status", "=", status, ""));
        }
        if(search == null && startDate != null && endDate == null && status != null){
            conditions.add(ComparisonOperator.getInstance("", "start_date", "=", startDate, ""));
            conditions.add(ComparisonOperator.getInstance(" and ", "status", "=", status, ""));
        }
        //three
        if(search != null && startDate != null && endDate != null && status == null){
            conditions.add(ComparisonOperator.getInstance("(", "title", "like ", "%"+search+"%", ""));
            conditions.add(ComparisonOperator.getInstance(" or ", "description", "like ", "%"+search+"%", ")"));
            conditions.add(ComparisonOperator.getInstance(" and ", "start_date", ">=", startDate, ""));
            conditions.add(ComparisonOperator.getInstance(" and ", "end_date", "<=", endDate, ""));
        }
        if(search != null && startDate != null && endDate == null && status != null){
            conditions.add(ComparisonOperator.getInstance("(", "title", "like ", "%"+search+"%", ""));
            conditions.add(ComparisonOperator.getInstance(" or ", "description", "like ", "%"+search+"%", ")"));
            conditions.add(ComparisonOperator.getInstance(" and ", "start_date", "=", startDate, ""));
            conditions.add(ComparisonOperator.getInstance(" and ", "status", "=", status, ""));
        }
        if(search != null && startDate == null && endDate != null && status != null){
            conditions.add(ComparisonOperator.getInstance("(", "title", "like ", "%"+search+"%", ""));
            conditions.add(ComparisonOperator.getInstance(" or ", "description", "like ", "%"+search+"%", ")"));
            conditions.add(ComparisonOperator.getInstance(" and ", "end_date", "=", endDate, ""));
            conditions.add(ComparisonOperator.getInstance(" and ", "status", "=", status, ""));
        }
        if(search == null && startDate != null && endDate != null && status != null){
            conditions.add(ComparisonOperator.getInstance(" and ", "start_date", ">=", startDate, ""));
            conditions.add(ComparisonOperator.getInstance(" and ", "end_date", "<=", endDate, ""));
            conditions.add(ComparisonOperator.getInstance(" and ", "status", "=", status, ""));
        }
        return conditions;
    }

}

class searchParam {
    public String search;
    public String startDate;
    public String endDate;
    public String status;
    public static searchParam searchParam;

    static searchParam getInstance(String search, String startDate, String endDate, String status){
        if(searchParam == null){
            searchParam item = new searchParam();
            item.search = search;
            item.startDate = startDate;
            item.endDate = endDate;
            item.status = status;
            return item;
        }
        return searchParam;
    }
}
