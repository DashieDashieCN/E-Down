package com.dashie.entity;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Records {
    Long latestTime;
    Integer lastPage;

    public String toText() {
        return latestTime + "-" + lastPage;
    }
}
