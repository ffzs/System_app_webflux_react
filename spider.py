import time

import requests
from bs4 import BeautifulSoup
from faker import Faker

f = Faker("zh-CN")


def get_headers():
    return {
        'user-agent': f.user_agent(),
    }


def get_img(url):
    response = requests.get(url, headers=get_headers()).text
    soup = BeautifulSoup(response, 'lxml')
    all_img = soup.find('ul', class_='artCont cl').find_all('img')
    all_src = ['https:'+img.get('src') for img in all_img]
    for item in all_src:
        avatar_file.writelines(item+'\n')
    avatar_file.flush()


def get_page(url):
    response = requests.get(url, headers=get_headers()).text
    soup = BeautifulSoup(response, 'lxml')
    all_txt = soup.find('div', class_='pMain').find_all('a', class_='img')
    all_page = ['https://www.woyaogexing.com'+txt.get('href') for txt in all_txt]
    for page in all_page:
        get_img(page)
        time.sleep(3)


if __name__ == '__main__':
    url = 'https://www.woyaogexing.com/touxiang'
    avatar_file = open("avatar.txt", 'a+', encoding='utf-8')
    get_page(url)
    avatar_file.close()