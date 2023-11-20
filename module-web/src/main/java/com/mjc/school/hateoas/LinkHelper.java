package com.mjc.school.hateoas;

import com.mjc.school.controller.impl.AuthorRestController;
import com.mjc.school.controller.impl.CommentRestController;
import com.mjc.school.controller.impl.NewsRestController;
import com.mjc.school.controller.impl.TagRestController;
import com.mjc.school.service.dto.*;
import org.springframework.hateoas.EntityModel;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

public class LinkHelper {

    private LinkHelper() {
    }

    public static void addLinksToComment(EntityModel<CommentDtoResponse> commentModel) {
        CommentDtoResponse content = commentModel.getContent();
        if (content == null) return;
        commentModel.add(linkTo(methodOn(CommentRestController.class).getById(content.id())).withSelfRel());
        commentModel.add(linkTo(methodOn(NewsRestController.class).getById(content.newsId())).withRel("news"));
    }

    public static void addLinksToAuthor(EntityModel<AuthorDtoResponse> authorModel) {
        AuthorDtoResponse content = authorModel.getContent();
        if (content == null) return;
        authorModel.add(linkTo(methodOn(AuthorRestController.class).getById(content.id())).withSelfRel());
    }

    public static void addLinksToAuthorWithNewsAmount(EntityModel<AuthorWithNewsResponse> authorModel) {
        AuthorWithNewsResponse content = authorModel.getContent();
        if (content == null) return;
        authorModel.add(linkTo(methodOn(AuthorRestController.class).getById(content.id())).withSelfRel());
    }

    public static void addLinksToTag(EntityModel<TagDtoResponse> tagModel) {
        TagDtoResponse content = tagModel.getContent();
        if (content == null) return;
        tagModel.add(linkTo(methodOn(TagRestController.class).getById(content.id())).withSelfRel());
    }

    public static void addLinksToNews(EntityModel<NewsDtoResponse> newsModel) {
        NewsDtoResponse content = newsModel.getContent();
        if (content == null) return;
        newsModel.add(linkTo(methodOn(NewsRestController.class).getById(content.id())).withSelfRel());
        newsModel.add(linkTo(methodOn(NewsRestController.class).getAuthorByNewsId(content.id())).withRel("author"));
        newsModel.add(linkTo(methodOn(NewsRestController.class).getTagsByNewsId(content.id())).withRel("tags"));
        newsModel.add(linkTo(methodOn(NewsRestController.class).getCommentsByNewsId(content.id())).withRel("comments"));
    }
}
