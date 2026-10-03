package io.jonathanlee.switchyard_api.mappers;

import io.jonathanlee.switchyard_api.domain.models.Flag;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface FlagsMapper {

  @Select(
      """
      SELECT id, key, enabled, created_at, updated_at
      FROM flags
      ORDER BY key
      """)
  List<Flag> findAll();

  @Select(
      """
      SELECT id, key, enabled, created_at, updated_at
      FROM flags
      WHERE id = #{id}
      """)
  Optional<Flag> findById(UUID id);

  @Select(
      """
      SELECT id, key, enabled, created_at, updated_at
      FROM flags
      WHERE key = #{key}
      """)
  Optional<Flag> findByKey(String key);

  @Select(
      """
      INSERT INTO flags (key, enabled)
      VALUES (#{key}, #{enabled})
      RETURNING id, key, enabled, created_at, updated_at
      """)
  Optional<Flag> insert(Flag flag);

  @Select(
      """
      UPDATE flags
      SET key = #{flag.key},
          enabled = #{flag.enabled},
          updated_at = NOW()
      WHERE id = #{id}
      RETURNING id, key, enabled, created_at, updated_at
      """)
  Optional<Flag> updateById(@Param("id") UUID id, @Param("flag") Flag flag);

  @Select(
      """
      UPDATE flags
      SET key = #{flag.key},
          enabled = #{flag.enabled},
          updated_at = NOW()
      WHERE key = #{key}
      RETURNING id, key, enabled, created_at, updated_at
      """)
  Optional<Flag> updateByKey(@Param("key") String key, @Param("flag") Flag flag);

  @Select(
      """
      DELETE FROM flags
      WHERE id = #{id}
      RETURNING id, key, enabled, created_at, updated_at
      """)
  Optional<Flag> deleteById(UUID id);

  @Select(
      """
      DELETE FROM flags
      WHERE key = #{key}
      RETURNING id, key, enabled, created_at, updated_at
      """)
  Optional<Flag> deleteByKey(String key);
}
