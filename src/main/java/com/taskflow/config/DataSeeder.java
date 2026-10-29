package com.taskflow.config;

import com.taskflow.model.Priority;
import com.taskflow.model.Task;
import com.taskflow.model.TaskStatus;
import com.taskflow.repository.TaskRepository;
import java.time.LocalDate;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/** Ajoute quelques tâches d'exemple au démarrage pour que l'API ne soit pas vide. */
@Component
public class DataSeeder implements CommandLineRunner {

    private final TaskRepository repository;

    public DataSeeder(TaskRepository repository) {
        this.repository = repository;
    }

    @Override
    public void run(String... args) {
        if (repository.count() > 0) {
            return;
        }
        LocalDate today = LocalDate.now();

        add("Préparer la démo du projet", "Tester tous les endpoints avant de filmer",
                TaskStatus.IN_PROGRESS, Priority.HIGH, today.plusDays(1));
        add("Écrire le README", "Stack, routes, structure et exemples",
                TaskStatus.DONE, Priority.HIGH, today.minusDays(1));
        add("Ajouter les tests", "Tests MockMvc sur les routes principales",
                TaskStatus.TODO, Priority.MEDIUM, today.plusDays(3));
        add("Corriger le bug de pagination", "La page 2 renvoie une liste vide",
                TaskStatus.TODO, Priority.HIGH, today.minusDays(2));
        add("Réviser Spring Security", "Voir JWT et les rôles",
                TaskStatus.TODO, Priority.LOW, today.plusDays(14));
        add("Pousser le code sur GitHub", "Dépôt public avec captures d'écran",
                TaskStatus.IN_PROGRESS, Priority.MEDIUM, today.plusDays(2));
        add("Filmer le pitch d'une minute", "Ce que j'ai fait, appris, à améliorer",
                TaskStatus.TODO, Priority.MEDIUM, today.plusDays(4));
        add("Ranger le dossier Drive", "Vidéos, captures et lien GitHub",
                TaskStatus.DONE, Priority.LOW, null);
    }

    private void add(String title, String description, TaskStatus status, Priority priority, LocalDate dueDate) {
        Task task = new Task();
        task.setTitle(title);
        task.setDescription(description);
        task.setStatus(status);
        task.setPriority(priority);
        task.setDueDate(dueDate);
        repository.save(task);
    }
}