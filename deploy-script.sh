docker-compose -f docker-compose.prod.yml down

sudo docker rmi damu_backend_app

sudo docker-compose -f docker-compose.prod.yml up -d