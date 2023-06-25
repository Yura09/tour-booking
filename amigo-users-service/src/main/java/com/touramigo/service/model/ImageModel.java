package com.touramigo.service.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.touramigo.service.entity.user.Image;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

import static java.util.Objects.nonNull;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ImageModel {

    private UUID id;

    private String imageUrl;

    private String cloudUrl;

    private int width;

    private int height;

    public static ImageModel create(Image image) {
        ImageModel imageModel = new ImageModel();

        imageModel.setId(image.getId());

        if (nonNull(image.getImageURL())) {
            imageModel.setImageUrl(image.getImageURL());
        }

        if (nonNull(image.getCloudURL())) {

            imageModel.setCloudUrl(image.getCloudURL());
        }

        if (nonNull(image.getHeight())) {

            imageModel.setHeight(image.getHeight());
        }

        if (nonNull(image.getWidth())) {

            imageModel.setWidth(image.getWidth());
        }

        return imageModel;
    }
}
