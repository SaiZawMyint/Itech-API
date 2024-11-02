package com.itech.api.pkg.google.drive.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaginationDTO {
    private boolean hasNext;
    private String nextToken;
}
