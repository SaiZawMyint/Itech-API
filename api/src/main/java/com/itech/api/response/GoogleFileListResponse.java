package com.itech.api.response;


import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class GoogleFileListResponse<T> {
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private T list;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonProperty("has_next")
    private boolean hasNext;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonProperty("next_page_token")
    private String nextPageToken;
}
