package com.huhaoyu.thu.controller;

import com.huhaoyu.thu.common.Constants.Response;
import com.huhaoyu.thu.entity.Stadium;
import com.huhaoyu.thu.service.ScheduledTaskService;
import com.huhaoyu.thu.widget.VisibleEntityWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Created by huhaoyu
 * Created On 2017/1/23 下午4:43.
 */

@RestController
@RequestMapping(value = "stadium")
public class StadiumController {

    @Autowired
    private ScheduledTaskService taskService;

    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity getStadiums(@RequestParam(value = "stadium_id", required = false) Long stadiumId,
                                      @RequestParam(value = "name", required = false) String name,
                                      @RequestParam(value = "stadium_code", required = false) String stadiumCode,
                                      @RequestParam(value = "site_code", required = false) String siteCode,
                                      @RequestParam(value = "sport_type", required = false) Integer sportType) {
        List<Stadium> stadiums = taskService.getStadiumByProperties(stadiumId, name, stadiumCode, siteCode, sportType);
        Object data = VisibleEntityWrapper.createVisibleFieldsMap(stadiums);
        return ResponseEntity.ok(Response.Ok.createResponseMap(data));
    }

    @RequestMapping(method = RequestMethod.POST)
    public ResponseEntity createStadium(@RequestParam(value = "name") String name,
                                        @RequestParam(value = "stadium_code") String stadiumCode,
                                        @RequestParam(value = "site_code") String siteCode,
                                        @RequestParam(value = "sport_type") Integer sportType,
                                        @RequestParam(value = "exceptions", required = false) String exceptions,
                                        @RequestParam(value = "description", required = false) String description) {
        Stadium stadium = taskService.createStadium(name, stadiumCode, siteCode, sportType, exceptions, description);
        Object data = VisibleEntityWrapper.createVisibleFieldsMap(stadium);
        return ResponseEntity.ok(Response.Ok.createResponseMap(data));
    }

    @RequestMapping(method = RequestMethod.PUT)
    public ResponseEntity updateStadium(@RequestParam(value = "stadium_id") Long stadiumId,
                                        @RequestParam(value = "name", required = false) String name,
                                        @RequestParam(value = "stadium_code", required = false) String stadiumCode,
                                        @RequestParam(value = "site_code", required = false) String siteCode,
                                        @RequestParam(value = "sport_type", required = false) Integer sportType,
                                        @RequestParam(value = "exceptions", required = false) String exceptions,
                                        @RequestParam(value = "description", required = false) String description) {
        Stadium stadium = taskService.updateStadium(stadiumId, name, stadiumCode, siteCode, sportType, exceptions, description);
        Object data = VisibleEntityWrapper.createVisibleFieldsMap(stadium);
        return ResponseEntity.ok(Response.Ok.createResponseMap(data));
    }

    @RequestMapping(method = RequestMethod.DELETE)
    public ResponseEntity deleteStadium(@RequestParam(value = "stadium_id") Long stadiumId) {
        taskService.deleteStadium(stadiumId);
        return ResponseEntity.ok(Response.Ok.createResponseMap());
    }

}
