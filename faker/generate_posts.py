from faker import Faker
import psycopg2
from psycopg2 import Error

fake = Faker()

DB_NAME = "blog"
DB_USER = "postgres"
DB_PASSWORD = "postgres"
DB_HOST = "localhost"
DB_PORT = "5432"

try:
    connection = psycopg2.connect(
        database=DB_NAME,
        user=DB_USER,
        password=DB_PASSWORD,
        host=DB_HOST,
        port=DB_PORT
    )

    cursor = connection.cursor()
    
    for _ in range(1000):
        title = fake.sentence(nb_words=6)
        content = fake.text(max_nb_chars=5000)

        insert_query = """
            INSERT INTO posts (id_author, title, content)
            VALUES (1, %s, %s)
        """
        record_to_insert = (title, content)

        cursor.execute(insert_query, record_to_insert)

    connection.commit()
    print("success!")

except (Exception, Error) as error:
    print("err:", error)

finally:
    if connection:
        cursor.close()
        connection.close()
        print("close") 