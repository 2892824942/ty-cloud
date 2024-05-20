package com.ty.mid.framework.common.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class MenuTreeDTO extends BaseTreeDTO<MenuTreeDTO> {

    // @JsonIgnore
    private Long id;
    // @JsonIgnore
    private Long parentId;
    @JsonIgnore
    private Integer sequence = 0;

    private String path;
    private String component;
    private String name;
    private Meta meta;

    public MenuTreeDTO() {
    }

    public MenuTreeDTO(Long id, Long parentId, Integer sequence, String path, String component, String name, String icon) {
        this.id = id;
        this.parentId = parentId;
        this.sequence = sequence;
        this.path = path;
        this.name = name;
        this.meta = new Meta(name, icon);
    }

    public MenuTreeDTO(Long id, Long parentId, Integer sequence, String path, String component, String name, Meta meta) {
        this.id = id;
        this.parentId = parentId;
        this.sequence = sequence;
        this.path = path;
        this.component = component;
        this.name = name;
        this.meta = meta;
    }

    @Data
    public static class Meta {
        private String title;
        private String icon;

        public Meta() {
        }

        public Meta(String title, String icon) {
            this.title = title;
            this.icon = icon;
        }
    }
}
