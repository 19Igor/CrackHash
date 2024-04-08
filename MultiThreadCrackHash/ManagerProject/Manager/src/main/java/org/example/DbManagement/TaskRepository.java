package org.example.DbManagement;

import org.example.Model.DataBaseEntry;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("taskRepository")
public interface TaskRepository extends MongoRepository<DataBaseEntry, Integer> {
//    @Query(value = "{userID:'?0'}", fields = "{'_id': 1, 'userID': 1, 'taskID': 1, 'word': 1, 'status': 1, 'hash': 1, 'maxLength': 1, 'creationTime': 1, 'firstWord': 1, 'lastWord': 1}")
    @Query(value = "{userID: '?0'}")
    List<DataBaseEntry> findByUserID(String userID);

}
