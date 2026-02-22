package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.BinaryContent;

import com.sprint.mission.discodeit.repository.BinaryContentRepository;
import java.util.concurrent.locks.ReentrantLock;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.UUID;

@Repository
@ConditionalOnProperty(name = "discodeit.repository.type", havingValue = "file")
public class FileBinaryContentRepository implements BinaryContentRepository {

  private final Path DIRECTORY;
  private final String EXTENSION = ".ser";
  private final FileLockProvider fileLockProvider;

  public FileBinaryContentRepository(
      @Value("${discodeit.repository.file-directory}") String home,
      FileLockProvider fileLockProvider
  ) {
    this.DIRECTORY = Paths.get(
        home, //yaml에서 가져온 디렉토리 경로
        "file-data-map", //파일 저장 디렉토리 이름
        BinaryContent.class.getSimpleName() //클래스의 이름의 디렉토리
    );
    if (Files.notExists(DIRECTORY)) {
      try {
        Files.createDirectories(DIRECTORY);//디렉토리 없으면 생성
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    }
    this.fileLockProvider = fileLockProvider;
  }

  private Path resolvePath(UUID id) {
    return DIRECTORY.resolve(id + EXTENSION);
  }

  @Override
  public BinaryContent save(BinaryContent binaryContent) {
    Path path = resolvePath(binaryContent.getId());
    ReentrantLock lock = fileLockProvider.getLock(path);
    lock.lock();
    try (
        FileOutputStream fos = new FileOutputStream(path.toFile());
        ObjectOutputStream oos = new ObjectOutputStream(fos)
    ) {
      oos.writeObject(binaryContent);
    } catch (IOException e) {
      throw new RuntimeException(e);
    } finally {
      lock.unlock();
    }
    return binaryContent;
  }

  @Override
  public Optional<BinaryContent> find(UUID id) {//수정이 불가하기 때문에 굳이 락 안 넣음.
    BinaryContent binaryContentNullable = null;
    Path path = resolvePath(id);
    if (Files.exists(path)) {
      try (
          FileInputStream fis = new FileInputStream(path.toFile());
          ObjectInputStream ois = new ObjectInputStream(fis)
      ) {
        binaryContentNullable = (BinaryContent) ois.readObject();
      } catch (IOException | ClassNotFoundException e) {
        throw new RuntimeException(e);
      }
    }
    return Optional.ofNullable(binaryContentNullable);
  }

  @Override
  public void delete(UUID id) {
    Path path = resolvePath(id);
    try {
      Files.delete(path);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public boolean existsById(UUID id) {
    return Files.exists(resolvePath(id));
  }
}
