package com.example.calendriervacances;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Trouver les vues
        EditText emailEditText = findViewById(R.id.emailEditText);
        EditText passwordEditText = findViewById(R.id.passwordEditText);
        Button registerButton = findViewById(R.id.registerButton);
        TextView loginRedirectTextView = findViewById(R.id.loginRedirectTextView);

        // Créer une instance de DatabaseHelper
        final DatabaseHelper dbHelper = new DatabaseHelper(this);

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailEditText.getText().toString().trim();
                String password = passwordEditText.getText().toString().trim();

                // Vérifiez si les champs ne sont pas vides
                if (email.isEmpty() || password.isEmpty()) {
                    Toast.makeText(MainActivity.this, "Veuillez remplir tous les champs", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Vérifiez si l'email existe déjà
                if (dbHelper.userExists(email)) {
                    Toast.makeText(MainActivity.this, "L'utilisateur existe déjà, veuillez vous connecter", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Ajouter l'utilisateur
                long result = dbHelper.addUser(email, password);
                if (result != -1) {
                    Toast.makeText(MainActivity.this, "Inscription réussie, veuillez vous connecter", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(MainActivity.this, "Erreur lors de l'inscription, veuillez réessayer", Toast.LENGTH_SHORT).show();
                }
            }
        });

        loginRedirectTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });
    }
}
