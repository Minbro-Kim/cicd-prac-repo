package com.sprint.mission.discodeit.repository.file;

import com.sprint.mission.discodeit.entity.Message;
import com.sprint.mission.discodeit.repository.MessageRepository;
import java.util.concurrent.locks.ReentrantLock;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
@ConditionalOnProperty(name = "discodeit.repository.type", havingValue = "file")
public class FileMessageRepository implements MessageRepository {

  private final Path DIRECTORY;
  private final String EXTENSION = ".ser";
  private final FileLockProvider fileLockProvider;

  public FileMessageRepository(
      @Value("${discodeit.repository.file-directory}") String home,
      FileLockProvider fileLockProvider
  ) {
    this.DIRECTORY = Paths.get(home //yaml에서 가져온 디렉토리 경로
        , "file-data-map", Message.class.getSimpleName());
    if (Files.notExists(DIRECTORY)) {
      try {
        Files.createDirectories(DIRECTORY);
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
  public Message save(Message message) {
    Path path = resolvePath(message.getId());
    ReentrantLock lock = fileLockProvider.getLock(path);
    lock.lock();
    try (
        FileOutputStream fos = new FileOutputStream(path.toFile());
        ObjectOutputStream oos = new ObjectOutputStream(fos)
    ) {
      oos.writeObject(message);
    } catch (IOException e) {
      throw new RuntimeException(e);
    } finally {
      lock.unlock();
    }
    return message;
  }

  @Override
  public Optional<Message> findById(UUID id) {
    //일반 조회에서는 락이 필요없지만, 수정에 사용될 수 있기때문에 락, 두개를 분리하여 메서드를 구현하기도 한다.
    // 하지만 트랜잭셔널 내에서 의미있는 것이기 때문에 수정을 위한 락이라면 트랜잭셔널 어노테이션 적용이 필요하다
    Message messageNullable = null;
    Path path = resolvePath(id);
    ReentrantLock lock = fileLockProvider.getLock(path);
    lock.lock();
    if (Files.exists(path)) {
      try (
          FileInputStream fis = new FileInputStream(path.toFile());
          ObjectInputStream ois = new ObjectInputStream(fis)
      ) {
        messageNullable = (Message) ois.readObject();
      } catch (IOException | ClassNotFoundException e) {
        throw new RuntimeException(e);
      } finally {
        lock.unlock();
      }
    }
    return Optional.ofNullable(messageNullable);
  }

  @Override
  public List<Message> findAll() {
    try {
      return Files.list(DIRECTORY)
          .filter(path -> path.toString().endsWith(EXTENSION))
          .map(path -> {
            ReentrantLock lock = fileLockProvider.getLock(path);
            lock.lock();
            try (
                FileInputStream fis = new FileInputStream(path.toFile());
                ObjectInputStream ois = new ObjectInputStream(fis)
            ) {
              return (Message) ois.readObject();
            } catch (IOException | ClassNotFoundException e) {
              throw new RuntimeException(e);
            } finally {
              lock.unlock();
            }
          })
          .toList();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public List<Message> findAllByChannelId(UUID channelId) {
    return findAll().stream()
        .filter(m -> m.getChannelId().equals(channelId))
        .sorted(Comparator.comparing(Message::getCreatedAt))
        .toList();
  }

  @Override
  public Optional<Message> findLastMessageByChannelId(UUID channelId) {
    return findAll().stream()
        .filter(m -> m.getChannelId().equals(channelId))
        .max(Comparator.comparing(Message::getCreatedAt));
  }

  @Override
  public boolean existsById(UUID id) {
    Path path = resolvePath(id);
    return Files.exists(path);
  }

  @Override
  public void deleteById(UUID id) {
    Path path = resolvePath(id);
    try {
      Files.delete(path);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  @Override
  public void deleteByChannelId(UUID channelId) {
    findAllByChannelId(channelId).forEach(m -> deleteById(m.getId()));
  }
}
