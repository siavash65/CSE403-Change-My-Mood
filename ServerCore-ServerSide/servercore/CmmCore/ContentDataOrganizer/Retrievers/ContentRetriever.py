'''
Created on Nov 17, 2012

@author: hunlan
'''

PRIME_SCORE = 20
SECOND_SCORE = 5
MAX_SECOND = 4


PIC_FIRST_VIEW_HURDLE = 50
PIC_SECOND_VIEW_HURDLE = 100
PIC_THIRD_VIEW_HURDLE = 1000

FIRST_VIEW_HURDLE = 5000
SECOND_VIEW_HURDLE = 10000
THIRD_VIEW_HURDLE = 100000

VIEW_SCORE_ONE = 10
VIEW_SCORE_TWO = 10
VIEW_SCORE_THREE = 5

FIRST_COMMENT_HURDLE = 10
SECOND_COMMENT_HURDLE = 50

COMMENT_SCORE_ONE = 5
COMMENT_SCORE_TWO = 5

FIRST_FAV_HURDLE= 50
SECOND_FAV_HURDLE = 100
THIRD_FAV_HURDLE = 1000

FAV_SCORE_ONE = 10
FAV_SCORE_TWO = 10
FAV_SCORE_THREE = 5

FEED_BACK_WEIGHT = 200


HAPPY_PRIME_TERM = 'funny'
ROMANTIC_PRIME_TERM = 'love'

HAPPY_SEC_TERMS = ['hilarious', 'comical', 'humorous', 'entertaining']
ROMANTIC_SEC_TERMS = ['loving', 'romantic', 'affection', 'passionate']

def computeInitialScore(isPicture = True, isPrimeMatch=False, numSecond=0, \
                        views=0, comments=0, favs=0):
    numSecond = int(numSecond)
    views = int(views)
    comments = int(comments)
    favs = int(favs)
    
    firstVH = PIC_FIRST_VIEW_HURDLE if isPicture else FIRST_VIEW_HURDLE
    secondVH = PIC_SECOND_VIEW_HURDLE if isPicture else SECOND_VIEW_HURDLE
    thirdVH = PIC_THIRD_VIEW_HURDLE if isPicture else THIRD_VIEW_HURDLE
    
    
    score = 0
    if isPrimeMatch :
        score += PRIME_SCORE
        
    multi = numSecond if numSecond < MAX_SECOND else MAX_SECOND
    score += multi * SECOND_SCORE
    
    if views > firstVH:
        score += VIEW_SCORE_ONE
        
        if views > secondVH:
            score += VIEW_SCORE_TWO
            
            if views > thirdVH:
                score += VIEW_SCORE_THREE
    
    if comments > FIRST_COMMENT_HURDLE:
        score += COMMENT_SCORE_ONE
        
        if comments > SECOND_COMMENT_HURDLE:
            score += COMMENT_SCORE_TWO
    
    if favs > FIRST_FAV_HURDLE:
        score += FAV_SCORE_ONE
        
        if favs > SECOND_FAV_HURDLE:
            score += FAV_SCORE_TWO
            
            if favs > THIRD_FAV_HURDLE:
                score += FAV_SCORE_THREE           

    assert score <= 100
    return score
        
        
def computeFinalScore(initialScore, thumbs_up, total):
    assert total >= thumbs_up
    feedback_score = 1.0 * FEED_BACK_WEIGHT * thumbs_up / total
    final_score = 1.0 * (total + feedback_score) / 3

    return int(final_score)
    
    