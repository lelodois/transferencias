kafka-topics --create --topic com_movimentar  --partitions 1 --replication-factor 1 --if-not-exists --zookeeper zoo1
kafka-topics --create --topic evt_conta_mov  --partitions 1 --replication-factor 1 --if-not-exists --zookeeper zoo1
kafka-topics --create --topic evt_conta_mov_erro  --partitions 1 --replication-factor 1 --if-not-exists --zookeeper zoo1

kafka-console-producer --broker-list localhost:9092 --contaTopic conta-movimentar
    com_movimentar, evt_conta_mov, evt_conta_mov_erro;
