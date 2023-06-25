package com.touramigo.service.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserImageModel {

    private UUID id;

    private String imageUrl;

    private String cloudUrl;

    private int width;

    private int height;
}
