package com.example.calendriervacances;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.app.DatePickerDialog;
import android.widget.DatePicker;
import java.text.SimpleDateFormat;
import java.util.Locale;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class CalendarActivity extends AppCompatActivity {

    private GridView calendarGridView;
    private LinearLayout daysOfWeekLinearLayout;
    private TextView currentMonthTextView;
    private Button previousMonthButton;
    private Button nextMonthButton;

    private Calendar calendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);

        calendarGridView = findViewById(R.id.calendarGridView);
        daysOfWeekLinearLayout = findViewById(R.id.daysOfWeekLinearLayout);
        currentMonthTextView = findViewById(R.id.currentMonthTextView);
        previousMonthButton = findViewById(R.id.previousMonthButton);
        nextMonthButton = findViewById(R.id.nextMonthButton);
        Button addEventButton = findViewById(R.id.addEventButton);

        calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_MONTH, 1);

        initDaysOfWeek();
        initCalendarDays();
        initMonthNavigation();
        initGridViewClickListener();

        // Initialiser l'écouteur de clic pour le bouton "Ajouter un événement"
        addEventButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddEventDialog(-1);
            }
        });
    }


    private void initDaysOfWeek() {
        String[] daysOfWeek = {"Dim", "Lun", "Mar", "Mer", "Jeu", "Ven", "Sam"};
        for (String dayOfWeek : daysOfWeek) {
            TextView dayTextView = new TextView(this);
            dayTextView.setText(dayOfWeek);
            dayTextView.setGravity(Gravity.CENTER);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1);
            dayTextView.setLayoutParams(params);
            daysOfWeekLinearLayout.addView(dayTextView);
        }
    }

    private void initCalendarDays() {
        int monthStartDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK) - 1; // 0-indexed
        int daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);

        EventDatabaseHelper dbHelper = new EventDatabaseHelper(this);

        List<String> days = new ArrayList<>();
        for (int i = 0; i < monthStartDayOfWeek; i++) {
            days.add("");
        }
        for (int i = 1; i <= daysInMonth; i++) {
            calendar.set(Calendar.DAY_OF_MONTH, i);
            long dateInMillis = calendar.getTimeInMillis();

            String dayText = String.valueOf(i);
            if (!dbHelper.getEventsForDay(dateInMillis).isEmpty()) {
                dayText += "*";
            }

            days.add(dayText);
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, days);
        calendarGridView.setAdapter(adapter);

        updateCurrentMonthTextView();
    }

    private void initMonthNavigation() {
        previousMonthButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calendar.add(Calendar.MONTH, -1);
                initCalendarDays();
            }
        });

        nextMonthButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calendar.add(Calendar.MONTH, 1);
                initCalendarDays();
            }
        });
    }

    private void updateCurrentMonthTextView() {
        String[] monthNames = {"Janvier", "Février", "Mars", "Avril", "Mai", "Juin", "Juillet", "Août", "Septembre", "Octobre", "Novembre", "Décembre"};
        int monthIndex = calendar.get(Calendar.MONTH);
        int year = calendar.get(Calendar.YEAR);
        currentMonthTextView.setText(monthNames[monthIndex] + " " + year);
    }

    private void initGridViewClickListener() {
        calendarGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selectedDay = (String) parent.getItemAtPosition(position);
                if (!selectedDay.isEmpty()) {
                    int dayOfMonth = Integer.parseInt(selectedDay.replace("*", ""));
                    showEvents(dayOfMonth);
                }
            }
        });
    }

    private void showEvents(int dayOfMonth) {
        Calendar selectedDate = (Calendar) calendar.clone();
        selectedDate.set(Calendar.DAY_OF_MONTH, dayOfMonth);

        EventDatabaseHelper dbHelper = new EventDatabaseHelper(this);
        List<Event> events = dbHelper.getEventsForDay(selectedDate.getTimeInMillis());

        String eventsText = "Events for " + dayOfMonth + ":\n";
        for (Event event : events) {
            eventsText += "- " + event.getTitle() + ": " + event.getDescription() + "\n";
        }

        AlertDialog alertDialog = new AlertDialog.Builder(this)
                .setTitle("Events")
                .setMessage(eventsText)
                .setPositiveButton("OK", null)
                .create();
        alertDialog.show();
    }

    private void showAddEventDialog(int dayOfMonth) {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_add_event);

        final EditText eventTitleEditText = dialog.findViewById(R.id.eventTitleEditText);
        final EditText eventDescriptionEditText = dialog.findViewById(R.id.eventDescriptionEditText);
        final Button eventDateButton = dialog.findViewById(R.id.eventDateButton);
        Button cancelButton = dialog.findViewById(R.id.cancelButton);
        Button confirmButton = dialog.findViewById(R.id.confirmButton);

        final Calendar eventDate = Calendar.getInstance();

        eventDateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        CalendarActivity.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                                eventDate.set(year, month, dayOfMonth);
                                eventDateButton.setText(new SimpleDateFormat("dd/MM/yyyy", Locale.FRENCH).format(eventDate.getTime()));
                            }
                        }, eventDate.get(Calendar.YEAR), eventDate.get(Calendar.MONTH), eventDate.get(Calendar.DAY_OF_MONTH));
                datePickerDialog.show();
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = eventTitleEditText.getText().toString();
                String description = eventDescriptionEditText.getText().toString();

                Event event = new Event(title, description, eventDate.getTimeInMillis());
                EventDatabaseHelper dbHelper = new EventDatabaseHelper(CalendarActivity.this);
                dbHelper.addEvent(event);

                dialog.dismiss();
                initCalendarDays();
            }
        });

        dialog.show();
    }

}
