package rs.ac.bg.etf;

public enum DayOfWeek {
    MONDAY, TUESDAY, WEDNESDAY, THURSDAY, FRIDAY;

    public static DayOfWeek fromString(String day) {
        switch (day) {
            case "Monday":
                return MONDAY;
            case "Tuesday":
                return TUESDAY;
            case "Wednesday":
                return WEDNESDAY;
            case "Thursday":
                return THURSDAY;
            case "Friday":
                return FRIDAY;
            default:
                return null;
        }
    }
}
