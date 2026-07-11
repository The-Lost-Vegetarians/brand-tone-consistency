# import requests
# from bs4 import BeautifulSoup

# url = "https://www.tesla.com"
# response = requests.get(url)

# soup = BeautifulSoup(response.text, 'html.parser')

# whatever = soup.find_all('div', class_='page-homepage')
# for what in whatever:
#     print(what.get_text().strip())

import instaloader

# Initialize Instaloader
L = instaloader.Instaloader()

# Profile to scrape
profile = instaloader.Profile.from_username(L.context, "nike")

# Loop through posts
for post in profile.get_posts():
    print(post.caption)
    print(post.date)
    print("-" * 50)


