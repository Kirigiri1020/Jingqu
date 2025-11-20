package com.example.jingqu.repository;

import com.example.jingqu.entity.ScenicSpot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ScenicSpotRepository extends JpaRepository<ScenicSpot, Long> {
    List<ScenicSpot> findByStatusOrderByCreateTimeDesc(Boolean status);
    
    List<ScenicSpot> findByIsRecommendedAndStatusOrderByCreateTimeDesc(Boolean isRecommended, Boolean status);
    
    @Query("SELECT s FROM ScenicSpot s WHERE s.status = true ORDER BY s.createTime DESC")
    List<ScenicSpot> findActiveScenicSpots();
    
    @Query("SELECT s FROM ScenicSpot s WHERE s.isRecommended = true AND s.status = true ORDER BY s.createTime DESC")
    List<ScenicSpot> findRecommendedScenicSpots();
    
    /**
     * 按评分倒序获取所有景区
     */
    @Query("SELECT s FROM ScenicSpot s WHERE s.status = true ORDER BY s.count DESC, s.createTime DESC")
    List<ScenicSpot> findAllByCountDesc();
    
    /**
     * 根据标签查找相似景区
     * 排除指定的景区ID，匹配所有标签（只要有一个相同就返回）
     * 按标签匹配度和创建时间排序
     */
    @Query(value = "SELECT s.* FROM scenic_spot s " +
           "WHERE s.status = true " +
           "AND s.id != :excludeId " +
           "AND (:tags IS NULL OR " +
           "     EXISTS (SELECT 1 FROM (SELECT TRIM(SUBSTRING_INDEX(SUBSTRING_INDEX(:tags, ',', numbers.n), ',', -1)) AS tag " +
           "                           FROM (SELECT 1 n UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5) numbers " +
           "                           WHERE CHAR_LENGTH(:tags) - CHAR_LENGTH(REPLACE(:tags, ',', '')) >= numbers.n - 1) tags_list " +
           "          WHERE s.tags LIKE CONCAT('%', tags_list.tag, '%'))) " +
           "ORDER BY " +
           "(SELECT COUNT(*) FROM (SELECT TRIM(SUBSTRING_INDEX(SUBSTRING_INDEX(:tags, ',', numbers.n), ',', -1)) AS tag " +
           "                      FROM (SELECT 1 n UNION SELECT 2 UNION SELECT 3 UNION SELECT 4 UNION SELECT 5) numbers " +
           "                      WHERE CHAR_LENGTH(:tags) - CHAR_LENGTH(REPLACE(:tags, ',', '')) >= numbers.n - 1) tags_list " +
           " WHERE s.tags LIKE CONCAT('%', tags_list.tag, '%')) DESC, " +
           "s.create_time DESC " +
           "LIMIT :limit", 
           nativeQuery = true)
    List<ScenicSpot> findSimilarScenicSpots(@Param("excludeId") Long excludeId, 
                                           @Param("tags") String tags, 
                                           @Param("limit") int limit);
    
    /**
     * 筛选景区
     * 支持按名称、评分范围、价格范围、地理位置筛选
     */
    @Query("SELECT s FROM ScenicSpot s WHERE " +
           "(:title IS NULL OR s.title LIKE %:title%) AND " +
           "(:minCount IS NULL OR s.count >= :minCount) AND " +
           "(:maxCount IS NULL OR s.count <= :maxCount) AND " +
           "(:minPrice IS NULL OR s.price >= :minPrice) AND " +
           "(:maxPrice IS NULL OR s.price <= :maxPrice) AND " +
           "(:address IS NULL OR s.address LIKE %:address%) AND " +
           "s.status = true " +
           "ORDER BY s.createTime DESC")
    List<ScenicSpot> filterScenicSpots(@Param("title") String title,
                                      @Param("minCount") Double minCount,
                                      @Param("maxCount") Double maxCount,
                                      @Param("minPrice") Double minPrice,
                                      @Param("maxPrice") Double maxPrice,
                                      @Param("address") String address);
}
