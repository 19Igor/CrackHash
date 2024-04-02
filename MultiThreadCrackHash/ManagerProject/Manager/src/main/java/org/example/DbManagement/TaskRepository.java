package org.example.DbManagement;

import org.example.Model.DataBaseEntry;
import org.example.Model.Task;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface TaskRepository extends MongoRepository<DataBaseEntry, Integer> {
}
