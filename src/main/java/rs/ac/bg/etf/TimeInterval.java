package rs.ac.bg.etf;

import java.util.Objects;

public class TimeInterval {

    private int startHour;
    private int startMinute;
    private int endHour;
    private int endMinute;

    public TimeInterval(int startHour, int startMinute, int endHour, int endMinute) {
        this.startHour = startHour;
        this.startMinute = startMinute;
        this.endHour = endHour;
        this.endMinute = endMinute;
    }


    public int getStartHour() {
        return startHour;
    }

    public void setStartHour(int startHour) {
        this.startHour = startHour;
    }

    public int getStartMinute() {
        return startMinute;
    }

    public void setStartMinute(int startMinute) {
        this.startMinute = startMinute;
    }

    public int getEndHour() {
        return endHour;
    }

    public void setEndHour(int endHour) {
        this.endHour = endHour;
    }

    public int getEndMinute() {
        return endMinute;
    }

    public void setEndMinute(int endMinute) {
        this.endMinute = endMinute;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TimeInterval that = (TimeInterval) o;
        return startHour == that.startHour && startMinute == that.startMinute && endHour == that.endHour && endMinute == that.endMinute;
    }

    @Override
    public int hashCode() {
        return Objects.hash(startHour, startMinute, endHour, endMinute);
    }

    @Override
    public String toString() {
        return "[" +
                startHour +
                ":" + startMinute +
                "-" + endHour +
                ":" + endMinute +
                ']';
    }
}
