http://localhost:8080/actuator/prometheus
[]: # 
[]: # http://localhost:9090/graph
[]: # 
[]: # http://localhost:3000/
[]: # 
[]: # http://localhost:3000/dashboards
[]: #           
[]: # http://localhost:3000/d/000000012/grafana-play-home?orgId=1&refresh=5s
[]: # 
[]: # http://localhost:3000/d/000000012/grafana-play-home
[]: # 
[]: # http://localhost:3000/explore
[]: #                                               
# This file documents the Prometheus metrics endpoint exposed by the Spring Boot application.
# The endpoint `/actuator/prometheus` provides application metrics in a format compatible with Prometheus.
# 
# To access the metrics, ensure the application is running locally on port 8080.
# 
# Example usage:
# 1. Start the Spring Boot application.
# 2. Open a browser or use a tool like `curl` to visit the following URL:
#    http://localhost:8080/actuator/prometheus
# 3. Configure Prometheus to scrape metrics from this endpoint by adding it to the `scrape_configs` section
#    in your Prometheus configuration file.
#
# Example Prometheus configuration:
# ```yaml
# scrape_configs:
#   - job_name: 'spring-boot-local'
#     metrics_path: '/actuator/prometheus'
#     static_configs:
#       - targets: ['localhost:8080']
# ```
#
# Ensure that the `spring-boot-starter-actuator` dependency is included in your project to enable this endpoint.
http://localhost:8080/actuator/prometheus

```code
kubectl create namespace monitoring
helm repo add prometheus-community https://prometheus-community.github.io/helm-charts
helm repo add grafana https://grafana.github.io/helm-charts
helm repo update

helm install prometheus prometheus-community/prometheus --namespace monitoring

helm install grafana grafana/grafana --namespace monitoring
kubectl get pods -n monitoring
kubectl port-forward svc/grafana -n monitoring 3000:80


kubectl -n monitoring get configmap prometheus-server -o yaml > prometheus-server-configmap.yaml
編輯 prometheus.yml，在 scrape_configs 裡添加您的 Spring Boot 配置：

scrape_configs:
- job_name: 'prometheus'
  static_configs:
    - targets: ['localhost:9090']
- job_name: 'spring-boot-local'
  metrics_path: '/actuator/prometheus'
  static_configs:
    - targets: ['192.168.1.106:8080']
 kubectl apply -f D:\work\workspace\springboot3-all-in-one\prometheus-new-values.yaml



相關重設指令
kubectl -n monitoring delete configmap springboot-scrape-config
kubectl -n monitoring delete pod -l app.kubernetes.io/component=server
kubectl -n monitoring create configmap springboot-scrape-config --from-file=springboot.yaml=D:\work\workspace\springboot3-all-in-one\scrape-config.yaml
kubectl -n monitoring get configmap prometheus-server -o yaml > prometheus-server-configmap.yaml
kubectl -n monitoring delete pod -l app.kubernetes.io/component=server



kubectl port-forward svc/prometheus-server -n monitoring 9090:80
helm uninstall prometheus -n monitoring
release "prometheus" uninstalled
helm install prometheus prometheus-community/prometheus --namespace monitoring -f D:\work\workspace\springboot3-all-in-one\prometheus-custom-values.yaml


```