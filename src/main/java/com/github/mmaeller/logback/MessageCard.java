package com.github.mmaeller.logback;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.io.Serializable;
import lombok.Builder;
import lombok.Data;

@Data
@Builder(builderClassName = "Builder")
class MessageCard implements Serializable {

  private static final long serialVersionUID = -6361271204116407071L;

  @JsonProperty("@type")
  private final String type = "MessageCard";

  @JsonProperty("@context")
  private final String context = "http://schema.org/extensions";

  private String title;
  private String text;
  private String themeColor;
}
