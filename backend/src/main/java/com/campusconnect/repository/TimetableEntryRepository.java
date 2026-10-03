package com.campusconnect.repository;

import com.campusconnect.entity.TimetableEntry;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TimetableEntryRepository extends JpaRepository<TimetableEntry,Long>{
    List<TimetableEntry> findByDepartmentCodeAndSemesterOrderByDayOfWeekAscStartTimeAsc(
            String departmentCode, int semester);
}
