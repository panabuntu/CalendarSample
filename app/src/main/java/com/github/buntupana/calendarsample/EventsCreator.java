package com.github.buntupana.calendarsample;

public class EventsCreator {

//    public static List<Event> addEvents(int month, int year) {
//        Calendar currentCalender = Calendar.getInstance();
//        currentCalender.set(Calendar.DAY_OF_MONTH, 1);
//        Date firstDayOfMonth = currentCalender.getTime();
//        List<Event> events = new ArrayList<>();
//        for (int i = 0; i < 20; i++) {
//            currentCalender.setTime(firstDayOfMonth);
//            if (month > -1) {
//                currentCalender.set(Calendar.MONTH, month);
//            }
//            if (year > -1) {
//                currentCalender.set(Calendar.ERA, GregorianCalendar.AD);
//                currentCalender.set(Calendar.YEAR, year);
//            }
//            currentCalender.add(Calendar.DATE, i);
////            setToMidnight(currentCalender);
//            long timeInMillis = currentCalender.getTimeInMillis();
//
//            events.addAll(getEvents(timeInMillis, i));
//
//        }
//        return events;
//    }
//
//    private static List<Event> getEvents(long timeInMillis, int day) {
//        if (day < 2) {
//            return Arrays.asList(new Event(Color.argb(255, 169, 68, 65), timeInMillis, "Event at " + new Date(timeInMillis)));
//        } else if (day > 2 && day <= 6) {
//            return Arrays.asList(
//                    new Event(ContextCompat.getColor(this, R.color.status_missing), timeInMillis, "Event at " + new Date(timeInMillis)));
//        } else {
//            return Arrays.asList(
//                    new Event(ContextCompat.getColor(this, R.color.status_fully_approved), timeInMillis, "Event at " + new Date(timeInMillis)));
//        }
//    }

}
