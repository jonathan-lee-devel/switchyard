package io.jonathanlee.switchyard_api.mappers;

import io.jonathanlee.switchyard_api.domain.models.Audience;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface AudiencesMapper {

  @Select(
      """
      SELECT id, key, query, created_at, updated_at
      FROM audiences
      ORDER BY key
      """)
  List<Audience> findAll();

  @Select(
      """
      SELECT id, key, query, created_at, updated_at
      FROM audiences
      WHERE id = #{id}
      """)
  Optional<Audience> findById(UUID id);

  @Select(
      """
      SELECT id, key, query, created_at, updated_at
      FROM audiences
      WHERE key = #{key}
      """)
  Optional<Audience> findByKey(String key);

  @Select(
      """
      INSERT INTO audiences (key, query)
      VALUES (#{key}, #{query})
      RETURNING id, key, query, created_at, updated_at
      """)
  Optional<Audience> insert(Audience audience);

  @Select(
      """
      UPDATE audiences
      SET key = #{audience.key},
          query = #{audience.query},
          updated_at = NOW()
      WHERE id = #{id}
      RETURNING id, key, query, created_at, updated_at
      """)
  Optional<Audience> updateById(@Param("id") UUID id, @Param("audience") Audience audience);

  @Select(
      """
      UPDATE audiences
      SET key = #{audience.key},
          query = #{audience.query},
          updated_at = NOW()
      WHERE key = #{key}
      RETURNING id, key, query, created_at, updated_at
      """)
  Optional<Audience> updateByKey(@Param("key") String key, @Param("audience") Audience audience);

  @Select(
      """
      DELETE FROM audiences
      WHERE id = #{id}
      RETURNING id, key, query, created_at, updated_at
      """)
  Optional<Audience> deleteById(UUID id);

  @Select(
      """
      DELETE FROM audiences
      WHERE key = #{key}
      RETURNING id, key, query, created_at, updated_at
      """)
  Optional<Audience> deleteByKey(String key);
}
