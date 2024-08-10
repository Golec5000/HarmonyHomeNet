package bwp.pwr.daniel.rysz.harmonyhomenetlogic.mainLogicEntitys.forum.controller;

import bwp.pwr.daniel.rysz.harmonyhomenetlogic.mainLogicEntitys.forum.entity.Forum;
import bwp.pwr.daniel.rysz.harmonyhomenetlogic.mainLogicEntitys.forum.service.ForumService;
import bwp.pwr.daniel.rysz.harmonyhomenetlogic.mainLogicEntitys.forum.service.PostService;
import bwp.pwr.daniel.rysz.harmonyhomenetlogic.mainLogicEntitys.forum.service.TopicService;
import bwp.pwr.daniel.rysz.harmonyhomenetlogic.mainLogicEntitys.resident.service.ResidentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/bwp/api/v1/forum")
public class ForumController {

    private final TopicService topicService;
    private final ForumService forumService;
    private final PostService postService;
    private final ResidentService residentService;

    @GetMapping("/all")
    public ResponseEntity<List<Forum>> getAllForums() {
        return ResponseEntity.ok(forumService.findAll());
    }

}
