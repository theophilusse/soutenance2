urlDirSearch = "search"
inSearchSym = "q="
inGenderSym = "genre_exact="
inDateSym = "decade="
inPriceMinSym = null
inPriceMaxSym = null

xpWrapper = "/html/body/div[1]/div[4]/div[3]/div[2]/ul"
xpCell = "li"
xpTitle = "div[1]/a"

xpMarketplaceWrap = "//*[@id=\"master-release-marketplace\"]"
xpPrice = "header/div/span/span"

xpInfoWrap = "//*[@class=\"body_32Bo9\"]"
xpLeftElem = "th"
xpRightElem = "td/a"

ssl_enabled = false
include script/include/scrapDependency
include script/include/searchQuery

wrap = xpGet(page, xpWrapper)
if ((wrap == null) || (wrap.length() == 0))
    println(wrap.length())
    println("Error: No output found in \"" + searchQuery + "\"")
    println(xpWrapper)
	return
endif
block = wrap.get(0)
if (block == null)
    println("Error: No output found in \"" + searchQuery + "\"")
    return
endif
cell = xpGet(block, xpCell)
if (empty(cell) == true)
    println("Error: No output found in \"" + searchQuery + "\"")
    return
endif
lenCell = cell.length()
i = 0
while (i < lenCell)
    item = cell.get(i)
    title = ""
    gender = "0"
    date = ""
    price = ""
    source = ""

    block = xpGet(item, xpTitle)
    println(block.getAttribute("href")) // Debug
    source = baseDomain + block.get(0).getAttribute("href").substring(1)
    artistId = block.get(0).getAttribute("href")
    artistId = artistId.substring(1)
    artistId = artistId.split("[/]")
    artistId = artistId.get(-1)
    artistId = artistId.split("[-]")
    artistId = artistId.get(0)
    title = block.get(0).getText()
    itemPage = webClient.loadPage(source)
    if (itemPage == null)
        continue
    endif
    wrap = xpGet(itemPage, xpInfoWrap)
    if (wrap != null)
        lenInfo = wrap.length()
        j = 0
        while (j < lenInfo)
            blockLeft = xpGet(wrap.get(i), xpLeftElem)
            blockRight = xpGet(wrap.get(i), xpRightElem)
            strLeft = blockLeft.getText()
            strLeft = strLeft.replaceAll(":", "")
            strLeft = strLeft.trim()
            strLeft = strLeft.lowercase()
            strRight = blockRight.getText()

            if ((gender.length() == 0) && (strLeft == "genre"))
                gender = strRight
            else if ((date.length() == 0) && (strLeft == "année"))
                date = strRight
            endif
            j += 1
        wend
    endif
    market = xpGet(itemPage, xpMarketplaceWrap)
    if (market == null)
        println("Failed")
    else
        ePrice = xpGet(market, xpPrice)
        if (ePrice != null)
            price = ePrice.getText()
        endif
    endif
    outputTable.add(newRow(title, gender, date, price, source))
    pause(250)
    i += 1
wend
webClient.close()
