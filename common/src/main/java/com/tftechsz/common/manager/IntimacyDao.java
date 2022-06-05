package com.tftechsz.common.manager;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.tftechsz.common.entity.IntimacyEntity;

import java.util.List;

@Dao
public interface IntimacyDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Long insert(IntimacyEntity intimacy);


    @Query("SELECT * FROM intimacy")
    List<IntimacyEntity> getIntimacy();


    @Update
    int update(IntimacyEntity intimacy);

    @Delete
    int delete(IntimacyEntity intimacy);

    @Query("SELECT*FROM intimacy WHERE user_id = :userId AND self_id = :selfId")
    IntimacyEntity query(String userId,String selfId );

}
