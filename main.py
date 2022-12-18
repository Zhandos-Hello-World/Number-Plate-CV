import cv2


image = cv2.imread('image2.jpeg')

gray_image = cv2.cvtColor(image, cv2.COLOR_BGR2GRAY)
cv2.imwrite('./gray_image.png',gray_image)


edged = cv2.Canny(gray_image, 400, 400) 
cv2.imwrite('./edged.png',edged)

cnts,new = cv2.findContours(edged.copy(), cv2.RETR_LIST, cv2.CHAIN_APPROX_SIMPLE)
image1=image.copy()
cv2.drawContours(image1,cnts,-1,(0,255,0),3)
cv2.imwrite('./all_contours.png',image1)


cnts = sorted(cnts, key = cv2.contourArea, reverse = True)
image2 = image.copy()
cv2.drawContours(image2,cnts,-1,(0,255,0),3)


screenCnt = None
i=7
for c in cnts:
        perimeter = cv2.arcLength(c, True)
        approx = cv2.approxPolyDP(c, 0.018 * perimeter, True)
        if len(approx) == 4: 
                screenCnt = approx
                print(screenCnt)
                x,y,w,h = cv2.boundingRect(c) 
                new_img=image[y:y+h,x:x+w]
                cv2.imwrite('./'+str(i)+'.png',new_img)
                i+=1
                break


cv2.drawContours(image, [screenCnt], -1, (0, 255, 0), 3)
cv2.imwrite("./4.png", image)
cv2.imshow("image with detected license plate", image)
cv2.waitKey(0)
