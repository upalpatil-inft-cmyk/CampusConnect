package com.campusconnect.repository;

import com.campusconnect.entity.Department;
import com.campusconnect.entity.TimetableEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TimetableEntryRepository extends JpaRepository<TimetableEntry,Long>{
    List<TimetableEntry> findBySubject_DepartmentAndSubject_SemesterOrderByDayOfWeekAscStartTimeAsc(
            Department department, int semester);
}
