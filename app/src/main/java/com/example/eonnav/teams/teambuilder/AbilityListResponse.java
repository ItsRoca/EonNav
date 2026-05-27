package com.example.eonnav.teams.teambuilder;

import java.util.List;

public class AbilityListResponse {

    public List<Result> results;

    public static class Result {
        public String name;
        public String url;
    }
}