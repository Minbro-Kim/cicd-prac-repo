package com.sprint.mission.discodeit.repository.file;

import java.nio.file.Path;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;
import org.springframework.stereotype.Component;

@Component
public class FileLockProvider {

  //currentHashMap: 스레드 안전
  //
  private Map<Path, ReentrantLock> locks = new ConcurrentHashMap<>();

  //ReetrantLock: 명시적 Lock 객체
  public ReentrantLock getLock(Path path) {
    return locks.computeIfAbsent(path, k -> new ReentrantLock());
    //없으면 새로 만들어서 넣고, 있으면 기존 값 반환
    /*
    커런트 해시맵에서 coumputeIfAbsent
    - 내부적으로 버킷 단위 락 사용
    - 연산이 원자적(atomic)
    - 동시성 안전
     */
  }
}
